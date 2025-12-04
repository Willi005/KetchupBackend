package ketchupapp.ketchupbackend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ketchupapp.ketchupbackend.model.Order;
import ketchupapp.ketchupbackend.model.OrderItem;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class TicketService {

    // Configuración para 80mm (aprox 226 pts)
    // Margen reducido para maximizar espacio
    private static final Rectangle PAGE_SIZE = new Rectangle(226, 1200);

    // Fuentes estéticas
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
    private static final Font SUBHEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
    private static final Font DATA_FONT = FontFactory.getFont(FontFactory.COURIER, 8, Color.BLACK);
    private static final Font DATA_BOLD_FONT = FontFactory.getFont(FontFactory.COURIER_BOLD, 8, Color.BLACK);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.DARK_GRAY);

    // Fuentes Cocina (Grandes y claras)
    private static final Font KITCHEN_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
    private static final Font KITCHEN_ITEM = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

    public byte[] generateTicket(Order order) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PAGE_SIZE, 5, 5, 10, 10); // Márgenes: Izq, Der, Arr, Aba
            PdfWriter.getInstance(document, out);

            document.open();

            // ==========================================
            // TICKET CLIENTE
            // ==========================================

            // 1. Cabecera Centrada
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            addCenterCell(headerTable, "KETCHUP POS", HEADER_FONT);
            addCenterCell(headerTable, "Av. Siempre Viva 123", DATA_FONT);
            addCenterCell(headerTable, "Tel: +56 9 1234 5678", DATA_FONT);
            addCenterCell(headerTable, "--------------------------------", DATA_FONT);
            document.add(headerTable);

            // 2. Info Orden
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            // Fecha y Ticket alineados
            addLeftCell(infoTable, "FECHA: " + order.getOrderTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")), DATA_FONT);
            addRightCell(infoTable, "#" + order.getTicketNumber(), DATA_BOLD_FONT);

            // Cliente y Cajero
            PdfPCell clientCell = new PdfPCell(new Phrase("CTE: " + order.getClientName(), DATA_FONT));
            clientCell.setColspan(2); clientCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(clientCell);

            PdfPCell cashierCell = new PdfPCell(new Phrase("CAJ: " + order.getEmployeeName(), DATA_FONT));
            cashierCell.setColspan(2); cashierCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cashierCell);

            document.add(infoTable);
            document.add(new Paragraph("----------------------------------------------------------------", DATA_FONT));

            // 3. Items (Tabla perfecta: Cant | Desc | Total)
            PdfPTable itemTable = new PdfPTable(3);
            itemTable.setWidthPercentage(100);
            itemTable.setWidths(new float[]{0.7f, 2.5f, 1.2f}); // Proporciones de columna

            // Encabezados tabla
            addLeftCell(itemTable, "CANT", DATA_BOLD_FONT);
            addLeftCell(itemTable, "PRODUCTO", DATA_BOLD_FONT);
            addRightCell(itemTable, "TOTAL", DATA_BOLD_FONT);

            for (OrderItem item : order.getItems()) {
                addLeftCell(itemTable, String.valueOf(item.getQuantity()), DATA_FONT);
                addLeftCell(itemTable, item.getName(), DATA_FONT);
                addRightCell(itemTable, formatMoney(item.getPriceAtPurchase() * item.getQuantity()), DATA_FONT);
            }
            document.add(itemTable);

            document.add(new Paragraph("----------------------------------------------------------------", DATA_FONT));

            // 4. Totales
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(100);
            totalsTable.setWidths(new float[]{2, 1}); // Texto | Monto

            addRightTextCell(totalsTable, "SUBTOTAL:", DATA_FONT);
            addRightCell(totalsTable, formatMoney(order.getSubtotal()), DATA_FONT);

            addRightTextCell(totalsTable, "TOTAL A PAGAR:", SUBHEADER_FONT);
            addRightCell(totalsTable, formatMoney(order.getTotalAmount()), SUBHEADER_FONT);

            document.add(totalsTable);

            // 5. Info Pago
            document.add(new Paragraph(" ", DATA_FONT)); // Espacio
            PdfPTable paymentTable = new PdfPTable(2);
            paymentTable.setWidthPercentage(100);

            addLeftCell(paymentTable, "FORMA PAGO:", DATA_FONT);
            addRightCell(paymentTable, order.getPayment().getType().toString(), DATA_FONT);

            if (order.getPayment().getChangeGiven() > 0) {
                addLeftCell(paymentTable, "EFECTIVO:", DATA_FONT);
                addRightCell(paymentTable, formatMoney(order.getPayment().getAmountPaid()), DATA_FONT);
                addLeftCell(paymentTable, "VUELTO:", DATA_BOLD_FONT);
                addRightCell(paymentTable, formatMoney(order.getPayment().getChangeGiven()), DATA_BOLD_FONT);
            }
            document.add(paymentTable);

            // 6. Pie de página
            document.add(new Paragraph("\n"));
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            addCenterCell(footerTable, "¡GRACIAS POR SU COMPRA!", FOOTER_FONT);
            addCenterCell(footerTable, "Guarde su ticket para cambio", FOOTER_FONT);
            document.add(footerTable);

            // ==========================================
            // CORTE Y ESPACIO
            // ==========================================
            document.add(new Paragraph("\n\n- - - - - - - - - CORTE - - - - - - - - -\n\n", DATA_FONT));

            // ==========================================
            // TICKET COCINA (Minimalista y Grande)
            // ==========================================
            PdfPTable kitchenHeader = new PdfPTable(1);
            kitchenHeader.setWidthPercentage(100);

            // Invertir colores visualmente (simulado con negrita fuerte)
            addCenterCell(kitchenHeader, "*** COCINA ***", KITCHEN_HEADER);
            addCenterCell(kitchenHeader, "ORDEN #" + order.getTicketNumber(), KITCHEN_HEADER);
            addLeftCell(kitchenHeader, "Hora: " + order.getOrderTimestamp().format(DateTimeFormatter.ofPattern("HH:mm")), SUBHEADER_FONT);

            if (order.getKitchenNotes() != null && !order.getKitchenNotes().isEmpty()) {
                addLeftCell(kitchenHeader, "NOTA: " + order.getKitchenNotes(), SUBHEADER_FONT);
            }
            document.add(kitchenHeader);

            document.add(new Paragraph("--------------------------------", DATA_FONT));

            // Lista Items Cocina
            PdfPTable kitchenItems = new PdfPTable(2);
            kitchenItems.setWidthPercentage(100);
            kitchenItems.setWidths(new float[]{0.5f, 3f});

            for (OrderItem item : order.getItems()) {
                // Cantidad muy visible
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), KITCHEN_ITEM));
                qtyCell.setBorder(Rectangle.BOTTOM); // Línea sutil entre items
                qtyCell.setBorderWidthBottom(0.5f);
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qtyCell.setPaddingBottom(5);
                kitchenItems.addCell(qtyCell);

                PdfPCell nameCell = new PdfPCell(new Phrase(item.getName(), KITCHEN_ITEM));
                nameCell.setBorder(Rectangle.BOTTOM);
                nameCell.setBorderWidthBottom(0.5f);
                nameCell.setPaddingBottom(5);
                kitchenItems.addCell(nameCell);
            }
            document.add(kitchenItems);

            document.add(new Paragraph(".")); // Punto final para asegurar margen de corte

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando ticket", e);
        }
    }

    // --- Helpers para Tablas ---

    private void addCenterCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addLeftCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addRightCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addRightTextCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        // cell.setPaddingRight(10);
        table.addCell(cell);
    }

    private String formatMoney(double amount) {
        return "$" + (int) amount;
    }
}