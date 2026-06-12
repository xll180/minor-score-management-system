package com.minor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minor.entity.Course;
import com.minor.entity.ImportBatch;
import com.minor.entity.Score;
import com.minor.entity.Student;
import com.minor.mapper.CourseMapper;
import com.minor.mapper.ImportBatchMapper;
import com.minor.mapper.ScoreMapper;
import com.minor.mapper.StudentMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.annotation.Resource;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 报告导出服务
 * 使用 Apache POI 生成 Word 文档，使用 iText 生成 PDF 文档
 */
@Service
public class ReportExportService {

    @Resource
    private ImportBatchMapper importBatchMapper;

    @Resource
    private ScoreMapper scoreMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private StudentMapper studentMapper;

    /**
     * 导出 Word 格式的成绩分析报告
     *
     * @param batchId 导入批次ID
     * @return Word 文档的字节数组
     */
    public byte[] exportWord(Long batchId) {
        try {
            // 1. 查询导入批次信息
            ImportBatch batch = importBatchMapper.selectById(batchId);
            if (batch == null) {
                throw new RuntimeException("导入批次不存在");
            }

            // 2. 查询关联课程信息
            Course course = courseMapper.selectById(batch.getCourseId());

            // 3. 查询该批次的所有成绩记录
            List<Score> scoreList = scoreMapper.selectList(
                    new LambdaQueryWrapper<Score>().eq(Score::getImportBatchId, batchId));

            // 4. 创建 Word 文档
            XWPFDocument document = new XWPFDocument();

            // 5. 添加标题
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("辅修专业成绩分析报告");
            titleRun.setBold(true);
            titleRun.setFontSize(22);
            titleRun.setFontFamily("SimSun");
            titleRun.addBreak();

            // 添加空行
            XWPFParagraph blankParagraph = document.createParagraph();
            blankParagraph.createRun().setText("");

            // 6. 添加考试概况表格
            addParagraph(document, "一、考试概况", true, 16);
            XWPFTable summaryTable = document.createTable(6, 2);
            summaryTable.setWidthType(TableWidthType.PCT);
            summaryTable.setWidth("100%");

            // 设置表格列宽
            CTTblWidth tblWidth = summaryTable.getCTTbl().addNewTblPr().addNewTblW();
            tblWidth.setW(BigInteger.valueOf(5000));
            tblWidth.setType(STTblWidth.DXA);

            String[][] summaryData = {
                {"课程名称", course != null ? course.getCourseName() : "未知"},
                {"导入时间", batch.getCreateTime() != null ? batch.getCreateTime().toString() : "未知"},
                {"学生人数", String.valueOf(batch.getTotalCount() != null ? batch.getTotalCount() : 0)},
                {"平均分", batch.getAvgScore() != null ? batch.getAvgScore().setScale(2, RoundingMode.HALF_UP).toString() : "N/A"},
                {"最高分", batch.getMaxScore() != null ? batch.getMaxScore().setScale(2, RoundingMode.HALF_UP).toString() : "N/A"},
                {"最低分", batch.getMinScore() != null ? batch.getMinScore().setScale(2, RoundingMode.HALF_UP).toString() : "N/A"}
            };

            for (int i = 0; i < summaryData.length; i++) {
                XWPFTableRow row = summaryTable.getRow(i);
                XWPFTableCell cell0 = row.getCell(0);
                XWPFTableCell cell1 = row.getCell(1);
                setCellText(cell0, summaryData[i][0], true);
                setCellText(cell1, summaryData[i][1], false);
            }

            // 添加空行
            document.createParagraph().createRun().setText("");

            // 7. 添加成绩分布表格
            addParagraph(document, "二、成绩分布统计", true, 16);
            XWPFTable distributionTable = document.createTable(6, 3);
            distributionTable.setWidthType(TableWidthType.PCT);
            distributionTable.setWidth("100%");

            // 表头
            String[] distHeaders = {"分数段", "人数", "占比"};
            for (int i = 0; i < distHeaders.length; i++) {
                XWPFTableCell cell = distributionTable.getRow(0).getCell(i);
                setCellText(cell, distHeaders[i], true);
            }

            // 计算各分数段人数
            int[][] ranges = {{0, 59}, {60, 69}, {70, 79}, {80, 89}, {90, 100}};
            String[] rangeLabels = {"0-59", "60-69", "70-79", "80-89", "90-100"};
            int totalCount = scoreList.size();

            for (int i = 0; i < ranges.length; i++) {
                final int minScore = ranges[i][0];
                final int maxScore = ranges[i][1];
                int count = 0;
                for (Score score : scoreList) {
                    if (score.getScore() != null) {
                        double s = score.getScore().doubleValue();
                        if (s >= minScore && s <= maxScore) {
                            count++;
                        }
                    }
                }
                String percentage = totalCount > 0
                    ? BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP) + "%"
                    : "0%";

                XWPFTableRow row = distributionTable.getRow(i + 1);
                setCellText(row.getCell(0), rangeLabels[i], false);
                setCellText(row.getCell(1), String.valueOf(count), false);
                setCellText(row.getCell(2), percentage, false);
            }

            // 添加空行
            document.createParagraph().createRun().setText("");

            // 8. 添加及格率和优秀率统计
            addParagraph(document, "三、关键指标", true, 16);
            if (batch.getPassRate() != null) {
                addParagraph(document, "及格率（≥60分）：" + batch.getPassRate().setScale(1, RoundingMode.HALF_UP) + "%", false, 12);
            }
            if (batch.getExcellentRate() != null) {
                addParagraph(document, "优秀率（≥90分）：" + batch.getExcellentRate().setScale(1, RoundingMode.HALF_UP) + "%", false, 12);
            }
            document.createParagraph().createRun().setText("");

            // 9. 添加AI分析报告正文
            if (batch.getAiReport() != null && !batch.getAiReport().isEmpty()) {
                addParagraph(document, "四、AI分析报告", true, 16);
                XWPFParagraph aiParagraph = document.createParagraph();
                XWPFRun aiRun = aiParagraph.createRun();
                aiRun.setText(batch.getAiReport());
                aiRun.setFontSize(12);
                aiRun.setFontFamily("SimSun");
            }

            // 10. 输出字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("导出Word报告失败: " + e.getMessage(), e);
        }
    }

    /**
     * 导出 PDF 格式的成绩分析报告
     *
     * @param batchId 导入批次ID
     * @return PDF 文档的字节数组
     */
    public byte[] exportPdf(Long batchId) {
        try {
            // 1. 查询导入批次信息
            ImportBatch batch = importBatchMapper.selectById(batchId);
            if (batch == null) {
                throw new RuntimeException("导入批次不存在");
            }

            // 2. 查询关联课程信息
            Course course = courseMapper.selectById(batch.getCourseId());

            // 3. 查询该批次的所有成绩记录
            List<Score> scoreList = scoreMapper.selectList(
                    new LambdaQueryWrapper<Score>().eq(Score::getImportBatchId, batchId));

            // 4. 创建PDF文档
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4);
            PdfWriter.getInstance(pdfDoc, baos);
            pdfDoc.open();

            // 5. 创建中文字体
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 22, Font.BOLD);
            Font headingFont = new Font(baseFont, 16, Font.BOLD);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);

            // 6. 添加标题
            Paragraph titleParagraph = new Paragraph("辅修专业成绩分析报告", titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            pdfDoc.add(titleParagraph);
            pdfDoc.add(new Paragraph(" "));

            // 7. 添加考试概况表格
            Paragraph summaryHeading = new Paragraph("一、考试概况", headingFont);
            pdfDoc.add(summaryHeading);
            pdfDoc.add(new Paragraph(" "));

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(10f);
            summaryTable.setSpacingAfter(10f);

            String[][] summaryData = {
                {"课程名称", course != null ? course.getCourseName() : "未知"},
                {"导入时间", batch.getCreateTime() != null ? batch.getCreateTime().toString() : "未知"},
                {"学生人数", String.valueOf(batch.getTotalCount() != null ? batch.getTotalCount() : 0)},
                {"平均分", batch.getAvgScore() != null ? batch.getAvgScore().setScale(2, RoundingMode.HALF_UP).toString() : "N/A"},
                {"最高分", batch.getMaxScore() != null ? batch.getMaxScore().setScale(2, RoundingMode.HALF_UP).toString() : "N/A"},
                {"最低分", batch.getMinScore() != null ? batch.getMinScore().setScale(2, RoundingMode.HALF_UP).toString() : "N/A"}
            };

            for (String[] row : summaryData) {
                PdfPCell cellLabel = new PdfPCell(new Paragraph(row[0], boldFont));
                cellLabel.setPadding(5);
                PdfPCell cellValue = new PdfPCell(new Paragraph(row[1], normalFont));
                cellValue.setPadding(5);
                summaryTable.addCell(cellLabel);
                summaryTable.addCell(cellValue);
            }
            pdfDoc.add(summaryTable);
            pdfDoc.add(new Paragraph(" "));

            // 8. 添加成绩分布表格
            Paragraph distHeading = new Paragraph("二、成绩分布统计", headingFont);
            pdfDoc.add(distHeading);
            pdfDoc.add(new Paragraph(" "));

            PdfPTable distributionTable = new PdfPTable(3);
            distributionTable.setWidthPercentage(100);
            distributionTable.setSpacingBefore(10f);
            distributionTable.setSpacingAfter(10f);

            // 表头
            String[] distHeaders = {"分数段", "人数", "占比"};
            for (String header : distHeaders) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, boldFont));
                headerCell.setPadding(5);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                distributionTable.addCell(headerCell);
            }

            // 计算各分数段人数
            int[][] ranges = {{0, 59}, {60, 69}, {70, 79}, {80, 89}, {90, 100}};
            String[] rangeLabels = {"0-59", "60-69", "70-79", "80-89", "90-100"};
            int totalCount = scoreList.size();

            for (int i = 0; i < ranges.length; i++) {
                final int minScore = ranges[i][0];
                final int maxScore = ranges[i][1];
                int count = 0;
                for (Score score : scoreList) {
                    if (score.getScore() != null) {
                        double s = score.getScore().doubleValue();
                        if (s >= minScore && s <= maxScore) {
                            count++;
                        }
                    }
                }
                String percentage = totalCount > 0
                    ? BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP) + "%"
                    : "0%";

                PdfPCell cellRange = new PdfPCell(new Paragraph(rangeLabels[i], normalFont));
                cellRange.setPadding(5);
                cellRange.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell cellCount = new PdfPCell(new Paragraph(String.valueOf(count), normalFont));
                cellCount.setPadding(5);
                cellCount.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell cellPercent = new PdfPCell(new Paragraph(percentage, normalFont));
                cellPercent.setPadding(5);
                cellPercent.setHorizontalAlignment(Element.ALIGN_CENTER);

                distributionTable.addCell(cellRange);
                distributionTable.addCell(cellCount);
                distributionTable.addCell(cellPercent);
            }
            pdfDoc.add(distributionTable);
            pdfDoc.add(new Paragraph(" "));

            // 9. 添加关键指标
            Paragraph keyHeading = new Paragraph("三、关键指标", headingFont);
            pdfDoc.add(keyHeading);
            pdfDoc.add(new Paragraph(" "));

            if (batch.getPassRate() != null) {
                pdfDoc.add(new Paragraph("及格率（≥60分）：" + batch.getPassRate().setScale(1, RoundingMode.HALF_UP) + "%", normalFont));
            }
            if (batch.getExcellentRate() != null) {
                pdfDoc.add(new Paragraph("优秀率（≥90分）：" + batch.getExcellentRate().setScale(1, RoundingMode.HALF_UP) + "%", normalFont));
            }
            pdfDoc.add(new Paragraph(" "));

            // 10. 添加AI分析报告正文
            if (batch.getAiReport() != null && !batch.getAiReport().isEmpty()) {
                Paragraph aiHeading = new Paragraph("四、AI分析报告", headingFont);
                pdfDoc.add(aiHeading);
                pdfDoc.add(new Paragraph(" "));

                Paragraph aiParagraph = new Paragraph(batch.getAiReport(), normalFont);
                aiParagraph.setFirstLineIndent(24f);
                pdfDoc.add(aiParagraph);
            }

            pdfDoc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("导出PDF报告失败: " + e.getMessage(), e);
        }
    }

    /**
     * 添加带格式的段落
     */
    private void addParagraph(XWPFDocument document, String text, boolean bold, int fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontSize(fontSize);
        run.setFontFamily("SimSun");
    }

    /**
     * 设置表格单元格文本
     */
    private void setCellText(XWPFTableCell cell, String text, boolean bold) {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(bold);
        run.setFontSize(12);
        run.setFontFamily("SimSun");
    }
}