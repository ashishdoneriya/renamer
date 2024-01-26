package com.csetutorials.services;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class DuplicateFilesRemover {
	public static void removeDuplicates(String... paths) {
		Stack<File> stack = new Stack<>();
		for (String path: paths) {
			stack.push(new File(path));
		}
		Set<String> hashes1 = new HashSet<>();
		Set<String> hashes2 = new HashSet<>();
		while (!stack.isEmpty()) {
			File obj = stack.pop();
			if (obj.isDirectory()) {
				File[] files = obj.listFiles();
				if (files != null) {
					for (File file : files) {
						stack.push(file);
					}
				}
			} else if (obj.isFile()) {
				try {
					String hash1 = getHash1(obj);
					String hash2 = getHash2(obj);
					if (hashes1.contains(hash1) && hashes2.contains(hash2)) {
						System.out.println("Deleted : " + obj.getName());
						obj.delete();
					} else {
						hashes1.add(hash1);
						hashes2.add(hash2);
					}
				} catch (Exception e) {
					System.out.println("Problem while calculating hash of - " + obj.getAbsolutePath());
				}
			}
		}
	}

	public static String getHash1(File obj) throws NoSuchAlgorithmException, IOException {
		byte[] data = Files.readAllBytes(obj.toPath());
		byte[] hash = MessageDigest.getInstance("MD5").digest(data);
		return new BigInteger(1, hash).toString(16);
	}

	public static String getHash2(File obj) throws NoSuchAlgorithmException, IOException {
		byte[] data = Files.readAllBytes(obj.toPath());
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
		return new BigInteger(1, hash).toString(16);
	}


}