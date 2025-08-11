package com.proyecto.KASH.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.proyecto.KASH.Repository.foroRepositorio;
import com.proyecto.KASH.entidad.foro;
import jakarta.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ReporteForosControlador {

    @Autowired
    private foroRepositorio foroRepositorio;

    @GetMapping("/report-foros")
    public ResponseEntity<byte[]> generarReporteForos(
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String trimestre,
            HttpServletResponse response) {
        try {
            List<foro> foros = foroRepositorio.findAll();
            
            // Aplicar filtros si se proporcionan
            if (tema != null && !tema.isEmpty() && !tema.equals("todos")) {
                foros = foros.stream()
                        .filter(f -> f.getTema().equals(tema))
                        .collect(Collectors.toList());
            }
            
            if (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos")) {
                foros = foros.stream()
                        .filter(f -> String.valueOf(f.getTrimestre()).equals(trimestre))
                        .collect(Collectors.toList());
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // ======= HEADER CON LOGO Y TÍTULO ========
            PdfPTable header = new PdfPTable(3);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{1f, 3f, 2f});

            BaseColor azul = new BaseColor(3, 49, 75);

            // LOGO (actualiza la ruta si es necesario)
            Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png");
            logo.scaleToFit(50, 50);
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setBackgroundColor(azul);
            logoCell.setPadding(5);
            header.addCell(logoCell);

            // TÍTULO CENTRADO
            PdfPCell titulo = new PdfPCell(new Phrase("KASH", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.WHITE)));
            titulo.setBorder(Rectangle.NO_BORDER);
            titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            titulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titulo.setBackgroundColor(azul);
            header.addCell(titulo);

            // SUBTÍTULO A LA DERECHA
            PdfPCell sub = new PdfPCell(new Phrase("FOROS", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE)));
            sub.setHorizontalAlignment(Element.ALIGN_RIGHT);
            sub.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sub.setBorder(Rectangle.NO_BORDER);
            sub.setBackgroundColor(azul);
            sub.setPaddingRight(10);
            header.addCell(sub);

            doc.add(header);
            doc.add(new Paragraph("\n"));
            
            // Información de filtros aplicados
            if ((tema != null && !tema.isEmpty() && !tema.equals("todos")) || 
                (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos"))) {
                
                Paragraph filtrosInfo = new Paragraph("Filtros aplicados:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
                doc.add(filtrosInfo);
                
                if (tema != null && !tema.isEmpty() && !tema.equals("todos")) {
                    doc.add(new Paragraph("Tema: " + tema, new Font(Font.FontFamily.HELVETICA, 10)));
                }
                
                if (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos")) {
                    doc.add(new Paragraph("Trimestre: " + trimestre, new Font(Font.FontFamily.HELVETICA, 10)));
                }
                
                doc.add(new Paragraph("\n"));
            }

            // ========== TABLA DE FOROS CON CONTENIDO ==========
            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1f, 1.8f, 1.8f, 1.8f, 1.5f, 3f});

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

            String[] headers = {"ID", "Nombre", "Tema", "Fecha", "Usuario", "Contenido"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                cell.setBackgroundColor(azul);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                tabla.addCell(cell);
            }

            for (foro f : foros) {
                tabla.addCell(String.valueOf(f.getId()));
                tabla.addCell(f.getNombre());
                tabla.addCell(f.getTema());
                tabla.addCell(String.valueOf(f.getFecha()));
                tabla.addCell(String.valueOf(f.getIdUsuario()));

                PdfPCell contenidoCell = new PdfPCell(new Phrase(f.getContenido()));
                contenidoCell.setPadding(5);
                tabla.addCell(contenidoCell);
            }

            doc.add(tabla);
            doc.add(new Paragraph("\n"));

            // ========== GRÁFICO DE TEMAS CON PORCENTAJE ==========
            Map<String, Integer> conteo = new HashMap<>();
            int total = foros.size();

            for (foro f : foros) {
                conteo.put(f.getTema(), conteo.getOrDefault(f.getTema(), 0) + 1);
            }

            DefaultPieDataset dataset = new DefaultPieDataset();
            for (Map.Entry<String, Integer> e : conteo.entrySet()) {
                double porcentaje = (e.getValue() * 100.0) / total;
                String etiqueta = e.getKey() + String.format(" (%.1f%%)", porcentaje);
                dataset.setValue(etiqueta, e.getValue());
            }

            JFreeChart chart = ChartFactory.createPieChart(
                    "Porcentaje de Foros por Tema",
                    dataset,
                    true, true, false
            );

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
            plot.setLabelBackgroundPaint(Color.WHITE);
            plot.setSectionOutlinesVisible(false);
            plot.setSimpleLabels(true);

            ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(chartBaos, chart, 500, 300);
            Image grafico = Image.getInstance(chartBaos.toByteArray());
            grafico.setAlignment(Element.ALIGN_CENTER);
            doc.add(grafico);

            doc.close();

            byte[] pdfBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=reporte_foros.pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/report-foros-personalizado")
    public ResponseEntity<byte[]> generarReporteForosPersonalizado(
            @RequestParam("campos") List<String> campos,
            @RequestParam("tipo") String tipo,
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String trimestre) {
        try {
            List<foro> foros = foroRepositorio.findAll();
            
            // Aplicar filtros si se proporcionan
            if (tema != null && !tema.isEmpty() && !tema.equals("todos")) {
                foros = foros.stream()
                        .filter(f -> f.getTema().equals(tema))
                        .collect(Collectors.toList());
            }
            
            if (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos")) {
                foros = foros.stream()
                        .filter(f -> String.valueOf(f.getTrimestre()).equals(trimestre))
                        .collect(Collectors.toList());
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            if ("pdf".equals(tipo)) {
                Document doc = new Document(PageSize.A4);
                PdfWriter.getInstance(doc, baos);
                doc.open();
    
                // ======= HEADER CON LOGO Y TÍTULO ========
                PdfPTable header = new PdfPTable(3);
                header.setWidthPercentage(100);
                header.setWidths(new float[]{1f, 3f, 2f});
    
                BaseColor azul = new BaseColor(3, 49, 75);
    
                // LOGO
                Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png");
                logo.scaleToFit(50, 50);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setBackgroundColor(azul);
                logoCell.setPadding(5);
                header.addCell(logoCell);
    
                // TÍTULO CENTRADO
                PdfPCell titulo = new PdfPCell(new Phrase("KASH", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.WHITE)));
                titulo.setBorder(Rectangle.NO_BORDER);
                titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
                titulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                titulo.setBackgroundColor(azul);
                header.addCell(titulo);
    
                // SUBTÍTULO A LA DERECHA
                PdfPCell sub = new PdfPCell(new Phrase("Reporte Personalizado de FOROS", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE)));
                sub.setHorizontalAlignment(Element.ALIGN_RIGHT);
                sub.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sub.setBorder(Rectangle.NO_BORDER);
                sub.setBackgroundColor(azul);
                sub.setPaddingRight(10);
                header.addCell(sub);
    
                doc.add(header);
                doc.add(new Paragraph("\n"));
                
                // Información de filtros aplicados
                if ((tema != null && !tema.isEmpty() && !tema.equals("todos")) || 
                    (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos"))) {
                    
                    Paragraph filtrosInfo = new Paragraph("Filtros aplicados:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
                    doc.add(filtrosInfo);
                    
                    if (tema != null && !tema.isEmpty() && !tema.equals("todos")) {
                        doc.add(new Paragraph("Tema: " + tema, new Font(Font.FontFamily.HELVETICA, 10)));
                    }
                    
                    if (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos")) {
                        doc.add(new Paragraph("Trimestre: " + trimestre, new Font(Font.FontFamily.HELVETICA, 10)));
                    }
                    
                    doc.add(new Paragraph("\n"));
                }
    
                // ========== TABLA DE FOROS CON CAMPOS SELECCIONADOS ==========
                PdfPTable tabla = new PdfPTable(campos.size());
                tabla.setWidthPercentage(100);
    
                Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    
                // Encabezados
                for (String campo : campos) {
                    String headerText = "";
                    switch (campo) {
                        case "id": headerText = "ID"; break;
                        case "nombre": headerText = "Nombre"; break;
                        case "tema": headerText = "Tema"; break;
                        case "fecha": headerText = "Fecha"; break;
                        case "idUsuario": headerText = "Usuario"; break;
                        case "contenido": headerText = "Contenido"; break;
                        case "trimestre": headerText = "Trimestre"; break;
                        default: headerText = campo;
                    }
                    
                    PdfPCell cell = new PdfPCell(new Phrase(headerText, fontHeader));
                    cell.setBackgroundColor(azul);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    tabla.addCell(cell);
                }
    
                // Datos
                for (foro f : foros) {
                    for (String campo : campos) {
                        switch (campo) {
                            case "id": tabla.addCell(String.valueOf(f.getId())); break;
                            case "nombre": tabla.addCell(f.getNombre()); break;
                            case "tema": tabla.addCell(f.getTema()); break;
                            case "fecha": tabla.addCell(String.valueOf(f.getFecha())); break;
                            case "idUsuario": tabla.addCell(String.valueOf(f.getIdUsuario())); break;
                            case "contenido": 
                                PdfPCell contenidoCell = new PdfPCell(new Phrase(f.getContenido()));
                                contenidoCell.setPadding(5);
                                tabla.addCell(contenidoCell);
                                break;
                            case "trimestre": tabla.addCell(String.valueOf(f.getTrimestre())); break;
                            default: tabla.addCell("");
                        }
                    }
                }
    
                doc.add(tabla);
                doc.close();
    
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=reporte_foros_personalizado.pdf")
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .body(baos.toByteArray());
            } else {
                // Para Excel se implementaría aquí
                return ResponseEntity.badRequest().body("Formato no soportado".getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
