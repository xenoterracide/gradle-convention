// SPDX-FileCopyrightText: Copyright © 2025 - 2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0
// SPDX-License-Identifier: MIT

package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LibraryTest {

  @Test
  void greetReturnsHello() {
    var library = new Library();
    assertEquals("Hello World!", library.greet());
  }
}
