package com.example.jwt.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class QRGenerator {

	public static String generateQR(String data) {
		try {

			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 250, 250);

			BufferedImage image = new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);

			for (int x = 0; x < 250; x++) {
				for (int y = 0; y < 250; y++) {
					image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
				}
			}

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", stream);

			return Base64.getEncoder().encodeToString(stream.toByteArray());

		} catch (Exception e) {
			throw new RuntimeException("QR generation failed");
		}
	}
}