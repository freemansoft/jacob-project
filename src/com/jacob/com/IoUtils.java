package com.jacob.com;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class IoUtils {

	private IoUtils() {
	}

	public static Path copyFromResourcesToTempPath(String path) {
		return copyToTempPath(IoUtils.class.getResourceAsStream(path), getFileName(path));
	}

	public static Path copyToTempPath(InputStream source, String fileName) {
		try {
			final Path tmpPath = Files.createTempFile("", "_" + fileName);
			try (
				OutputStream target = Files.newOutputStream(tmpPath)
			) {
				copy(source, target);
			}
			tmpPath.toFile().deleteOnExit();
			return tmpPath;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void copy(InputStream source, OutputStream target) {
		try {
			final byte[] buff = new byte[256];
			int read;
			while ((read = source.read(buff, 0, buff.length)) != -1) {
				target.write(buff, 0, read);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static String getFileName(String path) {
		return Paths.get(path).getFileName().toString();
	}
}
