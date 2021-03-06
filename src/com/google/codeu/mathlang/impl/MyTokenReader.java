// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;


import java.io.IOException;
import java.lang.NumberFormatException;

import java.lang.StringBuilder;

import com.google.codeu.mathlang.core.tokens.Token;
import com.google.codeu.mathlang.core.tokens.StringToken;
import com.google.codeu.mathlang.core.tokens.NameToken;
import com.google.codeu.mathlang.core.tokens.SymbolToken;
import com.google.codeu.mathlang.core.tokens.NumberToken;
import com.google.codeu.mathlang.parsing.TokenReader;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {
  private StringBuilder currentToken = new StringBuilder();
  private int currentIndex = 0;
  private String source;

  public MyTokenReader(String source) {
    // Your token reader will only be given a string for input. The string will
    // contain the whole source (0 or more lines).
    this.source = source;
  }

  @Override
  public Token next() throws IOException {
    // Most of your work will take place here. For every call to |next| you should
    // return a token until you reach the end. When there are no more tokens, you
    // should return |null| to signal the end of input.

    // If for any reason you detect an error in the input, you may throw an IOException
    // which will stop all execution.
    while(remaining() > 0 && Character.isWhitespace(peek())) {
      read();
    }
    if (remaining() <= 0) {
      return null;
    } else if (peek() == '"') {
      return readWithQuotes();
    } else {
      return interpretToken(readWithNoQuotes());
    }
  }
  
  // Helper function to check remaining characters in source string.
  private int remaining() {
    return source.length() - currentIndex;
  }
  
  // Helper function to check the character at the current index of the source string.
  private char peek() throws IOException {
    if(currentIndex < source.length()) {
      return source.charAt(currentIndex);
    } else {
      throw new IOException("Peek error.");
    }
  }

  // Helper function to read the character at the current index of the source string.
  private char read() throws IOException {
    final char c = peek();
    currentIndex += 1;
    return c;
  }
  
  // Helper function to read text within quotes (notes).
  private Token readWithQuotes() throws IOException {
    currentToken.setLength(0);
    if (read() != '"') {
      throw new IOException("Strings must start with quote.");
    }
    while (peek() != '"') {
      currentToken.append(read());
    }
    read();
    return new StringToken(currentToken.toString());
  }
  
  // Helper function to read text outside of quotes.
  private String readWithNoQuotes() throws IOException {
    currentToken.setLength(0);
    while(remaining() > 0 && !Character.isWhitespace(peek())) {
    	if(currentToken.length() > 0) {
    		int previousIndex = currentToken.length() - 1;
    		if(currentToken.charAt(previousIndex) == '+') {
	    		return currentToken.toString();
    		} else if (currentToken.charAt(previousIndex) == '='){
	    		return currentToken.toString();
    		} else if(peek() == ';') {
	    		return currentToken.toString();
	    	} else if(peek() == '+') {
	    		return currentToken.toString();
	    	} else if(peek() == '=') {
	    		return currentToken.toString();
	    	} else {
	    		currentToken.append(read());
	    	}
    	} else {
    		currentToken.append(read());
    	}
    }
    return currentToken.toString();
  }
  
  // Helper function to interpret different types of tokens
  private Token interpretToken(String tokenContent) throws IOException{
	  // Check if single character or Token
	  if(tokenContent.length() == 1) {
		  switch(tokenContent.charAt(0)) {
		  	// Single character is a symbol
		  	case '+':
		  	case '-':
		  	case '=':
		  	case ';':
		  		SymbolToken returnSymbolToken = new SymbolToken(tokenContent.charAt(0));
				return returnSymbolToken;
		  	default:
		  		// Single character is a variable
		  		if(Character.isLetter(tokenContent.charAt(0))) {
		  			return new NameToken(tokenContent);
		  		// Single character is a number
		  		} else if (Character.isDigit(tokenContent.charAt(0))){
		  			return new NumberToken(Double.parseDouble(tokenContent));
		  		// Unexpected character
		  		} else {
		  			throw new IOException("ERROR: Unexpected character inputted.");
		  		}
		  }
		// Token is a function
	  } else if(tokenContent.equals("note") || tokenContent.equals("print") || tokenContent.equals("let")) {
		  NameToken returnNameToken = new NameToken(tokenContent);
		  return returnNameToken;
	  } else {
		  // Token is a number with multiple digits
		  try {
			  Double.parseDouble(tokenContent);
			  return new NumberToken(Double.parseDouble(tokenContent));
			// Token is a string
		  } catch(NumberFormatException e) {
			  return new StringToken(tokenContent);
		  }
		  
	  }
  }

}
