package fi.hel.allu.common.util;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfMerger {

  public static byte[] appendDocuments(byte[] destination, List<byte[]> sources) throws IOException {
    PDFMergerUtility pdfMerger = new PDFMergerUtility();
    pdfMerger.addSource(new ByteArrayInputStream(destination));
    sources.forEach(s -> pdfMerger.addSource(new ByteArrayInputStream(s)));
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    pdfMerger.setDestinationStream(output);
    pdfMerger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
    return output.toByteArray();
  }
}