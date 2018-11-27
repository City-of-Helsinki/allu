package fi.hel.allu.external.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfMerger {

  public static byte[] appendDocuments(byte[] destination, List<byte[]> sources) throws IOException {
    PDFMergerUtility pdfMerger = new PDFMergerUtility();
    try (PDDocument destinationDoc = PDDocument.load(destination)) {
      sources.forEach(s -> appendDocument(pdfMerger, destinationDoc, s));
      return toByteArray(destinationDoc);
    }
  }

  private static void appendDocument(PDFMergerUtility pdfMerger, PDDocument destination, byte[] source) {
    try (PDDocument sourceDoc = PDDocument.load(source)) {
      pdfMerger.appendDocument(destination, sourceDoc);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] toByteArray(PDDocument document) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    document.save(output);
    return output.toByteArray();
  }

}
