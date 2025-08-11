package com.proyecto.KASH.servicio;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Usuario;
import jakarta.annotation.PostConstruct;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicioImpl2 implements UsuarioServicio2 {

    @Autowired
    private Usuario2Repositorio Repositorio;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Usuario2Repositorio usuarioRepository;

    @Override
    public Optional<Usuario> findByUsuarioAndPass(String usuario, String pass) {
        return Repositorio.findByUsuarioAndPass(usuario, pass);
    }

    @Override
    public ByteArrayInputStream generarReporteUsuarios(List<Usuario> usuarios) throws Exception {
        Document documento = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(documento, out);
        documento.open();

        // HEADER en color #03314b con logo, texto "KASH" y "Usuarios"
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 4, 2});

        // Logo (ajusta la ruta según corresponda)
        Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png");
        logo.scaleToFit(50, 50);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(new BaseColor(3, 49, 75));
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(logoCell);

        // Texto "KASH"
        Font fontKash = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, BaseColor.WHITE);
        PdfPCell kashCell = new PdfPCell(new Phrase("KASH", fontKash));
        kashCell.setBorder(Rectangle.NO_BORDER);
        kashCell.setBackgroundColor(new BaseColor(3, 49, 75));
        kashCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        kashCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(kashCell);

        // Texto "Usuarios"
        PdfPCell usuariosCell = new PdfPCell(new Phrase("Usuarios", fontKash));
        usuariosCell.setBorder(Rectangle.NO_BORDER);
        usuariosCell.setBackgroundColor(new BaseColor(3, 49, 75));
        usuariosCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        usuariosCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(usuariosCell);

        documento.add(headerTable);
        documento.add(new Paragraph(" ")); // espacio

        // Tabla de usuarios
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new int[]{2, 2, 2, 2, 3, 2});

        Font fontHeader = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.WHITE);
        BaseColor azul = new BaseColor(3, 49, 75);

        Stream.of("Documento", "Rol", "Nombres", "Primer Apellido", "Correo", "Usuario")
                .forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                    cell.setBackgroundColor(azul);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                });

        for (Usuario u : usuarios) {
            tabla.addCell(String.valueOf(u.getIdUsuario()));
            tabla.addCell(u.getRolSeleccionado());
            tabla.addCell(u.getNombres());
            tabla.addCell(u.getPrimerA());
            tabla.addCell(u.getCorreo());
            tabla.addCell(u.getUsuario());
        }

        documento.add(tabla);
        documento.add(new Paragraph(" ")); // espacio

        // Gráfica de pastel (porcentajes)
        int total = usuarios.size();
        long aprendices = usuarios.stream().filter(u -> u.getRolSeleccionado().equalsIgnoreCase("aprendiz")).count();
        long asesores = usuarios.stream().filter(u -> u.getRolSeleccionado().equalsIgnoreCase("asesor")).count();

        DefaultPieDataset dataset = new DefaultPieDataset();
        if (aprendices > 0) {
            dataset.setValue("Aprendices", (double) aprendices / total * 100);
        }
        if (asesores > 0) {
            dataset.setValue("Asesores", (double) asesores / total * 100);
        }

        JFreeChart chart = ChartFactory.createPieChart("Distribución de Roles (%)", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new java.awt.Font("TimesRoman", java.awt.Font.PLAIN, 12));
        plot.setCircular(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}%"));
        plot.setSectionPaint("Aprendices", Color.BLUE);
        plot.setSectionPaint("Asesores", Color.GREEN);

        // Convertir la gráfica a imagen PDF
        BufferedImage chartImage = chart.createBufferedImage(500, 300);
        Image img = Image.getInstance(writer, chartImage, 1.0f);
        img.setAlignment(Element.ALIGN_CENTER);
        documento.add(img);

        documento.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return Repositorio.findByIdUsuario(id).orElse(null);
    }

    @Override
    public ByteArrayInputStream generarReporteUsuarioIndividual(Usuario usuario) throws Exception {
        Document documento = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(documento, out);
        documento.open();

        // Fuente personalizada
        Font fuenteTitulo = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD, BaseColor.WHITE);
        Font fuenteTexto = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK);
        Font fuenteCeldaAzul = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.WHITE);

        // Header azul con logo y título
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new int[]{1, 4});
        header.setSpacingAfter(20);

        // Logo
        Image logo = Image.getInstance("src/main/resources/static/img/logoKash.png"); // Ajusta ruta si hace falta
        logo.scaleToFit(60, 60);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(new BaseColor(0x03, 0x31, 0x4B)); // Azul
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(logoCell);

        // Título KASH
        PdfPCell tituloCell = new PdfPCell(new Phrase("KASH\nREPORTE INDIVIDUAL DE USUARIO", fuenteTitulo));
        tituloCell.setBorder(Rectangle.NO_BORDER);
        tituloCell.setBackgroundColor(new BaseColor(0x03, 0x31, 0x4B)); // Azul
        tituloCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tituloCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tituloCell.setPaddingLeft(10f);
        header.addCell(tituloCell);

        documento.add(header);

        // Tabla de datos
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setWidths(new float[]{3, 7});

        // Método auxiliar para crear celdas
        BiConsumer<String, String> agregarFila = (titulo, valor) -> {
            PdfPCell celdaTitulo = new PdfPCell(new Phrase(titulo, fuenteCeldaAzul));
            celdaTitulo.setBackgroundColor(new BaseColor(0x03, 0x31, 0x4B)); // Azul
            celdaTitulo.setPadding(8);
            celdaTitulo.setBorderWidth(1);
            tabla.addCell(celdaTitulo);

            PdfPCell celdaValor = new PdfPCell(new Phrase(valor != null ? valor : "-", fuenteTexto));
            celdaValor.setPadding(8);
            celdaValor.setBorderWidth(1);
            tabla.addCell(celdaValor);
        };

        agregarFila.accept("Documento:", String.valueOf(usuario.getIdUsuario()));
        agregarFila.accept("Nombre:", usuario.getNombres());
        agregarFila.accept("Primer Apellido:", usuario.getPrimerA());
        agregarFila.accept("Segundo Apellido:", usuario.getSegundoA());
        agregarFila.accept("Correo:", usuario.getCorreo());
        agregarFila.accept("Número:", usuario.getNumero());
        agregarFila.accept("Usuario:", usuario.getUsuario());
        agregarFila.accept("Contraseña:", usuario.getPass());
        agregarFila.accept("Tipo de Documento:", usuario.getTDocumento());
        agregarFila.accept("Fecha de Nacimiento:", String.valueOf(usuario.getFNacimiento()));
        agregarFila.accept("Trimestre:", String.valueOf(usuario.getTrimestre()));
        agregarFila.accept("Rol:", usuario.getRolSeleccionado());

        documento.add(tabla);
        documento.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public List<Usuario> cargarUsuariosDesdeCSV(MultipartFile archivo) throws IOException {
        List<Usuario> listaUsuarios = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

            for (CSVRecord record : csvParser) {
                try {
                    // Validaciones básicas de campos obligatorios
                    if (record.get("idUsuario").isEmpty() || record.get("rol").isEmpty() || record.get("nombres").isEmpty()
                            || record.get("primerApellido").isEmpty() || record.get("fechaNacimiento").isEmpty()
                            || record.get("tipoDocumento").isEmpty() || record.get("correo").isEmpty()
                            || record.get("numero").isEmpty() || record.get("usuario").isEmpty()
                            || record.get("contrasena").isEmpty()) {
                        continue;
                    }

                    Usuario usuario = new Usuario();

                    // Asignación de datos y conversión
                    usuario.setIdUsuario(Long.parseLong(record.get("idUsuario").trim()));
                    usuario.setRolSeleccionado(record.get("rol").trim());
                    usuario.setNombres(record.get("nombres").trim());
                    usuario.setPrimerA(record.get("primerApellido").trim());
                    usuario.setSegundoA(record.get("segundoApellido").trim());
                    usuario.setFNacimiento(LocalDate.parse(record.get("fechaNacimiento").trim(), formatter));
                    usuario.setTDocumento(record.get("tipoDocumento").trim());
                    usuario.setTrimestre(record.get("trimestre").trim());
                    usuario.setCorreo(record.get("correo").trim());
                    usuario.setNumero(record.get("numero").trim());
                    usuario.setUsuario(record.get("usuario").trim());
                    usuario.setPass(record.get("contrasena").trim());

                    listaUsuarios.add(usuario);

                    usuarioRepository.save(usuario);

                    // Enviar correo con TU mensaje
                    String asunto = "Bienvenido a KASH, " + usuario.getUsuario() + "!";
                    String mensaje = "Hola " + usuario.getUsuario() + ",\n\n"
                            + "Tu rol en KASH es: " + usuario.getRolSeleccionado() + ".\n\n"
                            + "En este espacio tendras exreperiencia en las asesorias, un asesor las impartira y los aprendices la tomaran"
                            + "\n\n"
                            + "También podrás disfrutar del foro y participar en otras actividades.\n\n"
                            + "¡Bienvenido a la comunidad de KASH!";

                    emailService.sendEmail(usuario.getCorreo(), asunto, mensaje);

                } catch (Exception e) {
                    System.out.println("Error procesando el registro: " + record.toString());
                    e.printStackTrace();
                }
            }
        }

        return listaUsuarios;
    }

    @Override
    public Optional<Usuario> findByUsuario(String usuario) {
        return (Optional<Usuario>) usuarioRepository.findByUsuario(usuario);
    }

    public void encriptarContrasenasTextoPlano() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario usuario : usuarios) {
            String contrasena = usuario.getPass();

            if (contrasena != null && !contrasena.startsWith("$2a$")) {
                // No está encriptada
                String contrasenaEncriptada = BCrypt.hashpw(contrasena, BCrypt.gensalt());
                usuario.setPass(contrasenaEncriptada);
                usuarioRepository.save(usuario);  // Guarda el cambio
            }
        }

        System.out.println("Contraseñas en texto plano actualizadas correctamente.");
    }

    @Override
    public ByteArrayInputStream generarPdfPersonalizado(List<Usuario> usuarios, List<String> campos) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Paragraph title = new Paragraph("Reporte de Usuarios Personalizado", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(campos.size());
        table.setWidthPercentage(100);

        // Encabezados
        for (String campo : campos) {
            PdfPCell header = new PdfPCell(new Phrase(nombreBonitoCampo(campo)));
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(header);
        }

        // Contenido
        for (Usuario u : usuarios) {
            for (String campo : campos) {
                String valor = obtenerValorCampo(u, campo);
                table.addCell(valor != null ? valor : "");
            }
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
    
     public String nombreBonitoCampo(String campo) {
        switch (campo) {
            case "idUsuario":
                return "Número de Documento";
            case "rolSeleccionado":
                return "Rol";
            case "nombres":
                return "Nombres";
            case "primerA":
                return "Primer Apellido";
            case "segundoA":
                return "Segundo Apellido";
            case "fNacimiento":
                return "Fecha de Nacimiento";
            case "tDocumento":
                return "Tipo de Documento";
            case "trimestre":
                return "Trimestre";
            case "correo":
                return "Correo";
            case "numero":
                return "Número";
            case "usuario":
                return "Usuario";
            default:
                return campo;
        }
    }

    public String obtenerValorCampo(Usuario u, String campo) {
        switch (campo) {
            case "idUsuario":
                return String.valueOf(u.getIdUsuario());
            case "rolSeleccionado":
                return u.getRolSeleccionado();
            case "nombres":
                return u.getNombres();
            case "primerA":
                return u.getPrimerA();
            case "segundoA":
                return u.getSegundoA();
            case "fNacimiento":
                return u.getFNacimiento() != null ? u.getFNacimiento().toString() : "";
            case "tDocumento":
                return u.getTDocumento();
            case "trimestre":
                return u.getTrimestre();
            case "correo":
                return u.getCorreo();
            case "numero":
                return u.getNumero();
            case "usuario":
                return u.getUsuario();
            default:
                return "";
        }
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return Repositorio.findAll();
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return Repositorio.findAll();
    }
    
    @Override
    public List<Usuario> buscarUsuarioPorFiltro(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return listarUsuarios();
        }
        
        String filtroLower = filtro.toLowerCase();
        return listarUsuarios().stream()
            .filter(u -> u.getNombres().toLowerCase().contains(filtroLower) ||
                   u.getPrimerA().toLowerCase().contains(filtroLower) ||
                   (u.getSegundoA() != null && u.getSegundoA().toLowerCase().contains(filtroLower)) ||
                   u.getCorreo().toLowerCase().contains(filtroLower) ||
                   u.getUsuario().toLowerCase().contains(filtroLower) ||
                   String.valueOf(u.getIdUsuario()).contains(filtroLower))
            .toList();
    }
    
    @Override
    public List<Usuario> obtenerUsuariosPorRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            return listarUsuarios();
        }
        
        return listarUsuarios().stream()
            .filter(u -> u.getRolSeleccionado().equalsIgnoreCase(rol))
            .toList();
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long idUsuario) {
        return Repositorio.findById(idUsuario);
    }
    
    @Override
    public void guardarUsuario(Usuario usuario) {
        Repositorio.save(usuario);
    }

}
