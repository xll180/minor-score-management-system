package com.minor.controller;

import com.minor.service.ReportExportService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 导出控制器
 * 提供 Word 和 PDF 格式的成绩分析报告导出接口
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Resource
    private ReportExportService reportExportService;

    /**
     * 导出 Word 格式的成绩分析报告
     *
     * @param batchId 导入批次ID
     * @return Word 文档字节流
     */
    @GetMapping("/word/{batchId}")
    public ResponseEntity<byte[]> exportWord(@PathVariable Long batchId) {
        byte[] wordBytes = reportExportService.exportWord(batchId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/msword"));
        headers.setContentDispositionFormData("attachment", "report_" + batchId + ".doc");

        return ResponseEntity.ok()
                .headers(headers)
                .body(wordBytes);
    }

    /**
     * 导出 PDF 格式的成绩分析报告
     *
     * @param batchId 导入批次ID
     * @return PDF 文档字节流
     */
    @GetMapping("/pdf/{batchId}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long batchId) {
        byte[] pdfBytes = reportExportService.exportPdf(batchId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report_" + batchId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}