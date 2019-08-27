package fi.hel.allu.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PdfMerger {

  public static byte[] appendDocuments(byte[] destination, List<byte[]> sources) throws IOException {
    PDFMergerUtility pdfMerger = new PDFMergerUtility();
    pdfMerger.addSource(new ByteArrayInputStream(destination));
    sources.forEach(s -> pdfMerger.addSource(new ByteArrayInputStream(s)));
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    pdfMerger.setDestinationStream(output);
    pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    return output.toByteArray();
  }
}
