// SPDX-FileCopyrightText: Copyright © 2024 - 2025 Caleb Cushing
//
// SPDX-License-Identifier: MIT

import com.xenoterracide.gradle.convention.publish.GithubPublicRepositoryConfiguration

// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: MIT

// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: MIT

plugins {
  id("com.gradle.plugin-publish")
  id("com.xenoterracide.gradle.convention.publish")
  id("com.xenoterracide.gradle.convention.coverage")
}

repositoryHost(GithubPublicRepositoryConfiguration())
repositoryHost.namespace.set("xenoterracide")

gradlePlugin {
  website.set(repositoryHost.repository.wesiteUrl.map { it.toString() })
  vcsUrl.set(repositoryHost.repository.cloneUrl.map { it.toString() })
}

publicationLegal {
  inceptionYear.set(2024)
  spdxLicenseIdentifiers.add("Apache-2.0")
}
