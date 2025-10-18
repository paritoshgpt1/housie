package io.github.paritoshgpt1.Housie.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final SpringTemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    public String renderHtml(String templateName, Map<String, Object> variables) {
        Context ctx = new Context();
        if (variables != null) variables.forEach(ctx::setVariable);
        return templateEngine.process(templateName, ctx);
    }

    public byte[] renderPdf(String html) throws IOException, InterruptedException {
        // Renderer selection: PDF_RENDERER=chromium|openhtml|auto (default: auto)
        String mode = System.getenv().getOrDefault("PDF_RENDERER",
                System.getProperty("PDF_RENDERER", "auto")).trim().toLowerCase();
        Exception last = null;
        switch (mode) {
            case "chromium":
                try { return renderWithChromium(html); } catch (Exception e) { last = e; }
                break;
            case "openhtml":
                try { return renderWithOpenHtmlToPdf(html); } catch (Exception e) { last = e; }
                break;
            default: // auto
                try { return renderWithChromium(html); } catch (Exception e) { last = e; }
                try { return renderWithOpenHtmlToPdf(html); } catch (Exception e) { last = e; }
        }
        IOException io = new IOException("All PDF renderers failed");
        if (last != null) io.addSuppressed(last);
        throw io;
    }

    // wkhtmltopdf support removed per request; Chromium + OpenHTMLtoPDF are used instead.

    private byte[] renderWithChromium(String html) throws IOException, InterruptedException {
        // Write HTML to a temporary file
        Path htmlFile = Files.createTempFile("tickets-", ".html");
        Files.write(htmlFile, html.getBytes(StandardCharsets.UTF_8));
        Path outPdf = Files.createTempFile("tickets-", ".pdf");

        String chrome = findChromiumBinary();
        if (chrome == null) {
            // Clean up temp files and fail fast to allow next fallback
            try { Files.deleteIfExists(htmlFile); } catch (Exception ignore) {}
            try { Files.deleteIfExists(outPdf); } catch (Exception ignore) {}
            throw new IOException("Chromium/Chrome binary not found (set CHROMIUM_BIN)");
        }
        java.util.List<String> cmd = new java.util.ArrayList<>();
        cmd.add(chrome);
        // Allow custom flags from env; otherwise use sensible defaults
        String flagsEnv = System.getenv("CHROME_FLAGS");
        if (flagsEnv != null && !flagsEnv.trim().isEmpty()) {
            for (String f : flagsEnv.trim().split("\\s+")) { if (!f.isEmpty()) cmd.add(f); }
        } else {
            cmd.add("--headless=new");
            cmd.add("--no-sandbox");
            cmd.add("--disable-gpu");
            cmd.add("--disable-dev-shm-usage");
        }
        cmd.add("--print-to-pdf-no-header");
        cmd.add("--print-to-pdf=" + outPdf.toAbsolutePath());
        cmd.add(htmlFile.toUri().toString());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        // Capture stderr for diagnostics
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        Thread errT = new Thread(() -> {
            try (InputStream es = p.getErrorStream()) {
                byte[] buf = new byte[4096];
                int r; while ((r = es.read(buf)) != -1) err.write(buf, 0, r);
            } catch (IOException ignore) {}
        });
        errT.start();
        int exit = p.waitFor();
        try { errT.join(100); } catch (InterruptedException ignore) {}
        if (exit != 0) {
            String msg = "";
            if (err.size() > 0) {
                try {
                    msg = err.toString("UTF-8");
                } catch (Exception ignore) {
                    msg = new String(err.toByteArray());
                }
            }
            log.warn("Chromium PDF failed: exit={} stderr={}", exit, msg);
            throw new IOException("chromium failed with exit code " + exit + (msg.isEmpty()?"":"; "+msg));
        }
        byte[] bytes = Files.readAllBytes(outPdf);
        try {
            Files.deleteIfExists(htmlFile);
            Files.deleteIfExists(outPdf);
        } catch (Exception ignore) {}
        return bytes;
    }

    private String findChromiumBinary() {
        String env = System.getenv("CHROMIUM_BIN");
        if (env == null || env.trim().isEmpty()) env = System.getenv("CHROME_PATH");
        if (env != null && !env.trim().isEmpty()) return env;
        String[] candidates = new String[]{
                "google-chrome",
                "google-chrome-stable",
                "chromium",
                "chromium-browser",
                "/usr/bin/google-chrome",
                "/usr/bin/google-chrome-stable",
                "/usr/bin/chromium",
                "/usr/bin/chromium-browser",
                // macOS app bundles
                "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
                "/Applications/Chromium.app/Contents/MacOS/Chromium",
                "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge"
        };
        for (String c : candidates) {
            try {
                Process p = new ProcessBuilder(c, "--version").start();
                if (p.waitFor() == 0) return c;
            } catch (Exception ignore) {}
        }
        return null;
    }

    public byte[] pdfToPng(byte[] pdfBytes) throws IOException {
        try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.pdmodel.PDDocument.load(pdfBytes)) {
            org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(doc);
            java.awt.image.BufferedImage image = renderer.renderImageWithDPI(0, 200f); // first page @ 200 DPI
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                javax.imageio.ImageIO.write(image, "PNG", baos);
                return baos.toByteArray();
            }
        }
    }

    private byte[] renderWithOpenHtmlToPdf(String html) throws IOException {
        // Pure Java fallback using OpenHTMLtoPDF (pdfbox)
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder = new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(baos);
            builder.run();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IOException("OpenHTMLtoPDF failed", e);
        }
    }
}
