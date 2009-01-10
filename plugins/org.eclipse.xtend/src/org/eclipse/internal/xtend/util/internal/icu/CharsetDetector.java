/*******************************************************************************
 * Copyright (c) 2005-2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.internal.xtend.util.internal.icu;
/**
This code was taken from the com.ibm.icu library (3.6.1). We needed to copy it to have not 
a dependency to this large library only the need of charset recognition.
(KTH 2007-07-30)
 
The com.ibm.icu library is published under Eclipse Public License V1.0.
*/
/**
*******************************************************************************
* Copyright (C) 2005-2006, International Business Machines Corporation and    *
* others. All Rights Reserved.                                                *
*******************************************************************************
*/

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;


/**
 * <code>CharsetDetector</code> provides a facility for detecting the
 * charset or encoding of character data in an unknown format.
 * The input data can either be from an input stream or an array of bytes.
 * The result of the detection operation is a list of possibly matching
 * charsets, or, for simple use, you can just ask for a Java Reader that
 * will will work over the input data.
 * <p/>
 * Character set detection is at best an imprecise operation.  The detection
 * process will attempt to identify the charset that best matches the characteristics
 * of the byte data, but the process is partly statistical in nature, and
 * the results can not be guaranteed to always be correct.
 * <p/>
 * For best accuracy in charset detection, the input data should be primarily
 * in a single language, and a minimum of a few hundred bytes worth of plain text
 * in the language are needed.  The detection process will attempt to
 * ignore html or xml style markup that could otherwise obscure the content.
 * <p/>
 * @draft ICU 3.4
 * @provisional This API might change or be removed in a future release.
 */
public class CharsetDetector {

//   Question: Should we have getters corresponding to the setters for inut text
//   and declared encoding?

//   A thought: If we were to create our own type of Java Reader, we could defer
//   figuring out an actual charset for data that starts out with too much English
//   only ASCII until the user actually read through to something that didn't look
//   like 7 bit English.  If  nothing else ever appeared, we would never need to
//   actually choose the "real" charset.  All assuming that the application just
//   wants the data, and doesn't care about a char set name.

    /**
     *   Constructor
     * 
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public CharsetDetector() {
    }

    /**
     * Set the declared encoding for charset detection.
     *  The declared encoding of an input text is an encoding obtained
     *  from an http header or xml declaration or similar source that
     *  can be provided as additional information to the charset detector.  
     *  A match between a declared encoding and a possible detected encoding
     *  will raise the quality of that detected encoding by a small delta,
     *  and will also appear as a "reason" for the match.
     * <p/>
     * A declared encoding that is incompatible with the input data being
     * analyzed will not be added to the list of possible encodings.
     * 
     *  @param encoding The declared encoding 
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public CharsetDetector setDeclaredEncoding(String encoding) {
        fDeclaredEncoding = encoding;
        return this;
    }
    
    /**
     * Set the input text (byte) data whose charset is to be detected.
     * 
     * @param in the input text of unknown encoding
     * 
     * @return This CharsetDetector
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public CharsetDetector setText(byte [] in) {
        fRawInput  = in;
        fRawLength = in.length;
        
        MungeInput();
        
        return this;
    }
    
    private static final int kBufSize = 8000;

    /**
     * Set the input text (byte) data whose charset is to be detected.
     *  <p/>
     *   The input stream that supplies the character data must have markSupported()
     *   == true; the charset detection process will read a small amount of data,
     *   then return the stream to its original position via
     *   the InputStream.reset() operation.  The exact amount that will
     *   be read depends on the characteristics of the data itself.
     *
     * @param in the input text of unknown encoding
     * 
     * @return This CharsetDetector
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    
    public CharsetDetector setText(InputStream in) throws IOException {
        fInputStream = in;
        fInputStream.mark(kBufSize);
        fRawInput = new byte[kBufSize];   // Always make a new buffer because the
                                          //   previous one may have come from the caller,
                                          //   in which case we can't touch it.
        fRawLength = 0;
        int remainingLength = kBufSize;
        while (remainingLength > 0 ) {
            // read() may give data in smallish chunks, esp. for remote sources.  Hence, this loop.
            int  bytesRead = fInputStream.read(fRawInput, fRawLength, remainingLength);
            if (bytesRead <= 0) {
                 break;
            }
            fRawLength += bytesRead;
            remainingLength -= bytesRead;
        }
        fInputStream.reset();
        
        MungeInput();                     // Strip html markup, collect byte stats.
        return this;
    }

  
    /**
     * Return the charset that best matches the supplied input data.
     * 
     * Note though, that because the detection 
     * only looks at the start of the input data,
     * there is a possibility that the returned charset will fail to handle
     * the full set of input data.
     * <p/>
     * Raise an exception if 
     *  <ul>
     *    <li>no charset appears to match the data.</li>
     *    <li>no input text has been provided</li>
     *  </ul>
     *
     * @return a CharsetMatch object representing the best matching charset, or
     *         <code>null</code> if there are no matches.
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public CharsetMatch detect() {
//   TODO:  A better implementation would be to copy the detect loop from
//          detectAll(), and cut it short as soon as a match with a high confidence
//          is found.  This is something to be done later, after things are otherwise
//          working.
        CharsetMatch matches[] = detectAll();
        
        if (matches == null || matches.length == 0) {
            return null;
        }
        
        return matches[0];
     }
    
    /**
     *  Return an array of all charsets that appear to be plausible
     *  matches with the input data.  The array is ordered with the
     *  best quality match first.
     * <p/>
     * Raise an exception if 
     *  <ul>
     *    <li>no charsets appear to match the input data.</li>
     *    <li>no input text has been provided</li>
     *  </ul>
     * 
     * @return An array of CharsetMatch objects representing possibly matching charsets.
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public CharsetMatch[] detectAll() {
        CharsetRecognizer csr;
        int               i;
        int               detectResults;
        int               confidence;
        ArrayList         matches = new ArrayList();
        
        //  Iterate over all possible charsets, remember all that
        //    give a match quality > 0.
        for (i=0; i<fCSRecognizers.size(); i++) {
            csr = (CharsetRecognizer)fCSRecognizers.get(i);
            detectResults = csr.match(this);
            confidence = detectResults & 0x000000ff;
            if (confidence > 0) {
                CharsetMatch  m = new CharsetMatch(this, csr, confidence);
                matches.add(m);
            }
        }
        Collections.sort(matches);      // CharsetMatch compares on confidence
        Collections.reverse(matches);   //  Put best match first.
        CharsetMatch [] resultArray = new CharsetMatch[matches.size()];
        resultArray = (CharsetMatch[]) matches.toArray(resultArray);
        return resultArray;
    }

    
    /**
     * Autodetect the charset of an inputStream, and return a Java Reader
     * to access the converted input data.
     * <p/>
     * This is a convenience method that is equivalent to
     *   <code>this.setDeclaredEncoding(declaredEncoding).setText(in).detect().getReader();</code>
     * <p/>
     *   For the input stream that supplies the character data, markSupported()
     *   must be true; the  charset detection will read a small amount of data,
     *   then return the stream to its original position via
     *   the InputStream.reset() operation.  The exact amount that will
     *    be read depends on the characteristics of the data itself.
     *<p/>
     * Raise an exception if no charsets appear to match the input data.
     * 
     * @param in The source of the byte data in the unknown charset.
     *
     * @param declaredEncoding  A declared encoding for the data, if available,
     *           or null or an empty string if none is available.
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public Reader getReader(InputStream in, String declaredEncoding) {
        fDeclaredEncoding = declaredEncoding;
        
        try {
            setText(in);
            
            CharsetMatch match = detect();
            
            if (match == null) {
                return null;
            }
            
            return match.getReader();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Autodetect the charset of an inputStream, and return a String
     * containing the converted input data.
     * <p/>
     * This is a convenience method that is equivalent to
     *   <code>this.setDeclaredEncoding(declaredEncoding).setText(in).detect().getString();</code>
     *<p/>
     * Raise an exception if no charsets appear to match the input data.
     * 
     * @param in The source of the byte data in the unknown charset.
     *
     * @param declaredEncoding  A declared encoding for the data, if available,
     *           or null or an empty string if none is available.
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public String getString(byte[] in, String declaredEncoding)
    {
        fDeclaredEncoding = declaredEncoding;
       
        try {
            setText(in);
            
            CharsetMatch match = detect();
            
            if (match == null) {
                return null;
            }
            
            return match.getString(-1);
        } catch (IOException e) {
            return null;
        }
    }

 
    /**
     * Get the names of all char sets that can be recognized by the char set detector.
     *
     * @return an array of the names of all charsets that can be recognized
     * by the charset detector.
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public static String[] getAllDetectableCharsets() {
        return fCharsetNames;
    }
    
    /**
     * Test whether or not input filtering is enabled.
     * 
     * @return <code>true</code> if input text will be filtered.
     * 
     * @see #enableInputFilter
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public boolean inputFilterEnabled()
    {
        return fStripTags;
    }
    
    /**
     * Enable filtering of input text. If filtering is enabled,
     * text within angle brackets ("<" and ">") will be removed
     * before detection.
     * 
     * @param filter <code>true</code> to enable input text filtering.
     * 
     * @return The previous setting.
     *
     * @draft ICU 3.4
     * @provisional This API might change or be removed in a future release.
     */
    public boolean enableInputFilter(boolean filter)
    {
        boolean previous = fStripTags;
        
        fStripTags = filter;
        
        return previous;
    }
    
    /**
     *  MungeInput - after getting a set of raw input data to be analyzed, preprocess
     *               it by removing what appears to be html markup.
     * 
     * @internal
     */
    private void MungeInput() {
        int srci = 0;
        int dsti = 0;
        byte b;
        boolean  inMarkup = false;
        int      openTags = 0;
        int      badTags  = 0;
        
        //
        //  html / xml markup stripping.
        //     quick and dirty, not 100% accurate, but hopefully good enough, statistically.
        //     discard everything within < brackets >
        //     Count how many total '<' and illegal (nested) '<' occur, so we can make some
        //     guess as to whether the input was actually marked up at all.
        if (fStripTags) {
            for (srci = 0; srci < fRawLength && dsti < fInputBytes.length; srci++) {
                b = fRawInput[srci];
                if (b == (byte)'<') {
                    if (inMarkup) {
                        badTags++;
                    }
                    inMarkup = true;
                    openTags++;
                }
                
                if (! inMarkup) {
                    fInputBytes[dsti++] = b;
                }
                
                if (b == (byte)'>') {
                    inMarkup = false;
                }        
            }
            
            fInputLen = dsti;
        }
        
        //
        //  If it looks like this input wasn't marked up, or if it looks like it's
        //    essentially nothing but markup abandon the markup stripping.
        //    Detection will have to work on the unstripped input.
        //
        if (openTags<5 || openTags/5 < badTags || 
                (fInputLen < 100 && fRawLength>600)) {
            int limit = fRawLength;
            
            if (limit > kBufSize) {
                limit = kBufSize;
            }
            
            for (srci=0; srci<limit; srci++) {
                fInputBytes[srci] = fRawInput[srci];
            }
            fInputLen = srci;
        }
        
        //
        // Tally up the byte occurence statistics.
        //   These are available for use by the various detectors.
        //
        Arrays.fill(fByteStats, (short)0);
        for (srci=0; srci<fInputLen; srci++) {
            int val = fInputBytes[srci] & 0x00ff;
            fByteStats[val]++;
        }
        
        fC1Bytes = false;
        for (int i = 0x80; i <= 0x9F; i += 1) {
            if (fByteStats[i] != 0) {
                fC1Bytes = true;
                break;
            }
        }
     }

    /**
     *  The following items are accessed by individual CharsetRecongizers during
     *     the recognition process
     * 
     * @internal
     */
    byte[]      fInputBytes =       // The text to be checked.  Markup will have been
                   new byte[kBufSize];  //   removed if appropriate.
    
    int         fInputLen;          // Length of the byte data in fInputText.
    
    short       fByteStats[] =      // byte frequency statistics for the input text.
                   new short[256];  //   Value is percent, not absolute.
                                    //   Value is rounded up, so zero really means zero occurences.
    
    boolean     fC1Bytes =          // True if any bytes in the range 0x80 - 0x9F are in the input;
                   false;
    
    String      fDeclaredEncoding;
    
    

    //
    //  Stuff private to CharsetDetector
    //
    byte[]               fRawInput;     // Original, untouched input bytes.
                                        //  If user gave us a byte array, this is it.
                                        //  If user gave us a stream, it's read to a 
                                        //  buffer here.
    int                  fRawLength;    // Length of data in fRawInput array.
    
    InputStream          fInputStream;  // User's input stream, or null if the user
                                        //   gave us a byte array.
     
    boolean              fStripTags =   // If true, setText() will strip tags from input text.
                           false;
    
    
    /**
     *  List of recognizers for all charsets known to the implementation.
     *
     * @internal
     */
    private static ArrayList fCSRecognizers = createRecognizers();
    private static String [] fCharsetNames;
    
   /**
     * Create the singleton instances of the CharsetRecognizer classes
     * <p>
     * Customized by oAW; see https://bugs.eclipse.org/bugs/show_bug.cgi?id=203919 
     * @internal
     */
	private static ArrayList createRecognizers() {
        ArrayList<CharsetRecognizer> recognizers = new ArrayList<CharsetRecognizer>();
     
        // :TRICKY: The sequence of Recognizers is built in reverse order of their
        //          generality. When detecting a charset, we use the charset with the
        //          highest scorig rate. If there are multiple charsets with the same
        //          scoring, the last one that fits best is used.
        
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_sjis());
        recognizers.add(new CharsetRecog_2022.CharsetRecog_2022JP());
        recognizers.add(new CharsetRecog_2022.CharsetRecog_2022CN());
        recognizers.add(new CharsetRecog_2022.CharsetRecog_2022KR());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_gb_18030());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_jp());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_euc.CharsetRecog_euc_kr());
        recognizers.add(new CharsetRecog_mbcs.CharsetRecog_big5());
        
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_da());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_de());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_en());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_es());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_fr());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_it());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_nl());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_no());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_pt());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_1_sv());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_2_cs());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_2_hu());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_2_pl());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_2_ro());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_5_ru());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_6_ar());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_7_el());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_I_he());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_8_he());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1251());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_windows_1256());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_KOI8_R());
        recognizers.add(new CharsetRecog_sbcs.CharsetRecog_8859_9_tr());
        
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_BE());
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_32_LE());
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_BE());
        recognizers.add(new CharsetRecog_Unicode.CharsetRecog_UTF_16_LE());

        recognizers.add(new CharsetRecog_UTF8());

        // Remove all Recognizers from this list which have unsupported character codes
        // in the current java runtime to avoid later problems with matching but unsupported
        // charsets.
        for (Iterator<CharsetRecognizer> iter = recognizers.iterator(); iter.hasNext();) {
            CharsetRecognizer recognizer = iter.next();
            try {
                Charset.forName(recognizer.getName());
            }
            catch (UnsupportedCharsetException ex) {
                iter.remove();
            }
        }
        
        // Create an array of all charset names, as a side effect.
        // Needed for the getAllDetectableCharsets() API.
        String[] charsetNames = new String [recognizers.size()];
        int out = 0;
        
        for (int i = 0; i < recognizers.size(); i++) {
            String name = ((CharsetRecognizer)recognizers.get(i)).getName();
            
            if (out == 0 || ! name.equals(charsetNames[out - 1])) {
                charsetNames[out++] = name;
            }
        }
        
        fCharsetNames = new String[out];
        System.arraycopy(charsetNames, 0, fCharsetNames, 0, out);
        
        return recognizers;
    }
}
