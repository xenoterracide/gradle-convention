---
# SPDX-FileCopyrightText: Copyright © 2026 Caleb Cushing
#
# SPDX-License-Identifier: CC-BY-NC-4.0

name: java
description: Write Java
license: CC-BY-NC-4.0
metadata:
author: Caleb Cushing
allowed-tools: ./gradlew
---

- prefer `import` over fully qualified class names inlined
  - do not use `*` imports
- prefer `var`, and RHS generics, unless a class cast would be required.
