// © Copyright 2024 Caleb Cushing
// SPDX-License-Identifier: Apache-2.0

package com.xenoterracide.gradle.convention.publish;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public interface PublicationLegal {
  Property<Number> getInceptionYear();

  SetProperty<String> getSpdxLicenseIdentifiers();
}
