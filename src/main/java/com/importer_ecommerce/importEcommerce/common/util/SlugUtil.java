package com.importer_ecommerce.importEcommerce.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for generating URL-friendly slugs from strings
 * Converts text to lowercase, removes special characters, and replaces spaces with hyphens
 */
public class SlugUtil {
    
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");
    
    /**
     * Generate a URL-friendly slug from the given string
     * 
     * @param input The input string to convert to slug
     * @return A URL-friendly slug
     */
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Normalize unicode characters (remove accents, etc.)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        
        // Convert to lowercase
        String slug = normalized.toLowerCase(Locale.ENGLISH);
        
        // Remove non-latin characters and replace with empty string
        slug = NON_LATIN.matcher(slug).replaceAll("");
        
        // Replace whitespace with hyphens
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        
        // Remove leading and trailing dashes
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        
        // Remove multiple consecutive dashes
        slug = slug.replaceAll("-+", "-");
        
        // Ensure slug is not empty
        if (slug.isEmpty()) {
            slug = "product";
        }
        
        return slug;
    }
    
    /**
     * Generate a unique slug by appending a number if the base slug already exists
     * 
     * @param baseSlug The base slug to make unique
     * @param existingSlugs List of existing slugs to check against
     * @return A unique slug
     */
    public static String generateUniqueSlug(String baseSlug, java.util.Set<String> existingSlugs) {
        if (baseSlug == null || baseSlug.isEmpty()) {
            baseSlug = "product";
        }
        
        String slug = baseSlug;
        int counter = 1;
        
        while (existingSlugs.contains(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
}
