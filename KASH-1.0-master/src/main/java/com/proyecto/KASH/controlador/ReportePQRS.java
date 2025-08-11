package com.proyecto.KASH.controlador;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.KASH.Repository.PQRSRepository;
import com.proyecto.KASH.entidad.PQRS;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reporte")
public class ReportePQRS {

    @Autowired
    private PQRSRepository pqrsRepository;

    @GetMapping("/coordinador/generar-reporte-pqrs")
    public ResponseEntity<byte[]> generarReportePQRS() throws DocumentException {
        try {
            List<PQRS> lista = pqrsRepository.findAll();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Header
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{1, 5});

            Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png");
            logo.scaleToFit(50, 50);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(0);
            logoCell.setBackgroundColor(new BaseColor(3, 49, 75));
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(logoCell);

            Font fontBlancoGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE);
            Font fontBlancoPeque = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.WHITE);

            Paragraph textoDerecha = new Paragraph();
            textoDerecha.add(new Chunk("KASH\n", fontBlancoGrande));
            textoDerecha.add(new Chunk("PQRS", fontBlancoPeque));

            PdfPCell textoCell = new PdfPCell(textoDerecha);
            textoCell.setBorder(0);
            textoCell.setBackgroundColor(new BaseColor(3, 49, 75));
            textoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            textoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(textoCell);

            document.add(header);
            document.add(Chunk.NEWLINE);

            // Título grande y negrilla
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph titulo = new Paragraph("Reporte de PQRS", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            // Tabla
            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 3, 3, 3, 3});

            // Encabezados de tabla con color
            String[] headers = {"ID", "Nombre", "Email", "Tipo", "Estado"};
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            BaseColor headerBgColor = new BaseColor(3, 49, 75);

            for (String encabezado : headers) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(encabezado, fontHeader));
                headerCell.setBackgroundColor(headerBgColor);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(headerCell);
            }

            // Datos
            for (PQRS p : lista) {
                tabla.addCell(String.valueOf(p.getId()));
                tabla.addCell(p.getNombre());
                tabla.addCell(p.getEmail());
                tabla.addCell(p.getTipo());
                tabla.addCell(p.getEstado().name());
            }

            document.add(tabla);
            document.add(Chunk.NEWLINE);

            // Pie chart con porcentajes y tamaño reducido
            Map<String, Long> conteoPorTipo = lista.stream()
                    .collect(Collectors.groupingBy(PQRS::getTipo, Collectors.counting()));

            DefaultPieDataset dataset = new DefaultPieDataset();
            conteoPorTipo.forEach(dataset::setValue);

            JFreeChart chart = ChartFactory.createPieChart(
                    "Distribución de PQRS por Tipo", dataset, true, true, false);

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                    "{0}: {1} ({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));

            chart.getPlot().setForegroundAlpha(0.8f); // Transparencia leve

            BufferedImage chartImage = chart.createBufferedImage(400, 300); // tamaño reducido
            ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", chartBaos);

            Image chartImg = Image.getInstance(chartBaos.toByteArray());
            chartImg.setAlignment(Image.ALIGN_CENTER);
            document.add(chartImg);

            document.close();

            HttpHeaders headersHttp = new HttpHeaders();
            headersHttp.setContentType(MediaType.APPLICATION_PDF);
            headersHttp.setContentDispositionFormData("filename", "reporte-pqrs.pdf");

            return ResponseEntity.ok()
                    .headers(headersHttp)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/coordinador/generar-reporte-pqrs-personalizado")
    public ResponseEntity<byte[]> generarReportePQRSPersonalizado(
            @RequestParam("campos") List<String> campos,
            @RequestParam("tipo") String tipo) throws Exception {
        
        List<PQRS> lista = pqrsRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        if ("pdf".equals(tipo)) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Header
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{1, 5});

            Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png");
            logo.scaleToFit(50, 50);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(0);
            logoCell.setBackgroundColor(new BaseColor(3, 49, 75));
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(logoCell);

            Font fontBlancoGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE);
            Font fontBlancoPeque = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.WHITE);

            Paragraph textoDerecha = new Paragraph();
            textoDerecha.add(new Chunk("KASH\n", fontBlancoGrande));
            textoDerecha.add(new Chunk("Reporte Personalizado de PQRS", fontBlancoPeque));

            PdfPCell textoCell = new PdfPCell(textoDerecha);
            textoCell.setBorder(0);
            textoCell.setBackgroundColor(new BaseColor(3, 49, 75));
            textoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            textoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(textoCell);

            document.add(header);
            document.add(Chunk.NEWLINE);

            // Título grande y negrilla
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph titulo = new Paragraph("Reporte Personalizado de PQRS", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            // Tabla con campos seleccionados
            PdfPTable tabla = new PdfPTable(campos.size());
            tabla.setWidthPercentage(100);
            
            // Encabezados de tabla con color
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            BaseColor headerBgColor = new BaseColor(3, 49, 75);

            for (String campo : campos) {
                String headerText = "";
                switch (campo) {
                    case "id": headerText = "ID"; break;
                    case "nombre": headerText = "Nombre"; break;
                    case "email": headerText = "Email"; break;
                    case "tipo": headerText = "Tipo"; break;
                    case "estado": headerText = "Estado"; break;
                    case "detalles": headerText = "Detalles"; break;
                    case "fecha": headerText = "Fecha"; break;
                    default: headerText = campo;
                }
                
                PdfPCell headerCell = new PdfPCell(new Paragraph(headerText, fontHeader));
                headerCell.setBackgroundColor(headerBgColor);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(headerCell);
            }

            // Datos
            for (PQRS p : lista) {
                for (String campo : campos) {
                    switch (campo) {
                        case "id": tabla.addCell(String.valueOf(p.getId())); break;
                        case "nombre": tabla.addCell(p.getNombre()); break;
                        case "email": tabla.addCell(p.getEmail()); break;
                        case "tipo": tabla.addCell(p.getTipo()); break;
                        case "estado": tabla.addCell(p.getEstado().name()); break;
                        case "detalles": tabla.addCell(p.getDetalles()); break;
                        case "fecha": tabla.addCell(p.getFecha().toString()); break;
                        default: tabla.addCell("");
                    }
                }
            }

            document.add(tabla);
            document.add(Chunk.NEWLINE);

            document.close();
            
            HttpHeaders headersHttp = new HttpHeaders();
            headersHttp.setContentType(MediaType.APPLICATION_PDF);
            headersHttp.setContentDispositionFormData("filename", "reporte-pqrs-personalizado.pdf");

            return ResponseEntity.ok()
                    .headers(headersHttp)
                    .body(baos.toByteArray());
        } else {
            // Para Excel se implementaría aquí
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
    }
}
