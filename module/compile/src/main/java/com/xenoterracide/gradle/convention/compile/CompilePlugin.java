// SPDX-FileCopyrightText: Copyright © 2024-2026 Caleb Cushing
//
// SPDX-License-Identifier: GPL-3.0-or-later WITH Classpath-exception-2.0

package com.xenoterracide.gradle.convention.compile;

import java.util.ArrayList;
import java.util.List;
import net.ltgt.gradle.errorprone.ErrorProneOptions;
import net.ltgt.gradle.errorprone.ErrorPronePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.compile.JavaCompile;

/**
 * Plugin for configuring Java compilation with Error Prone checks and sensible defaults.
 *
 * <p>This plugin:
 * <ul>
 *   <li>Applies the Error Prone plugin</li>
 *   <li>Configures compiler arguments for better diagnostics</li>
 *   <li>Enables numerous Error Prone checks as errors</li>
 *   <li>Configures NullAway for null safety</li>
 *   <li>Handles test vs production code differently</li>
 * </ul>
 *
 * <p>Note: This plugin does not configure the Java toolchain version, allowing
 * consuming projects to set it according to their needs (Gradle configurations are additive).
 */
public class CompilePlugin implements Plugin<Project> {

  private static final String COMPILE_JAVA = "compileJava";
  private static final String COMPILE_TEST_FIXTURES_JAVA = "compileTestFixturesJava";
  private static final String COMPILE_TEST_PREFIX = "compileTest";
  private static final String JAVA_TIME_DEFAULT_TIME_ZONE = "JavaTimeDefaultTimeZone";
  private static final String NULL_AWAY = "NullAway";
  private static final String COMMA = ",";

  private static final List<String> ANNOTATED_PACKAGES = List.of("com", "org", "net", "io", "dev", "graphql");

  private static final List<String> UNANNOTATED_SUBPACKAGES = List.of(
    "io.vavr",
    "org.apache.commons.lang3",
    "org.assertj",
    "org.eclipse.jgit"
  );

  private static final List<String> BASE_ERROR_PRONE_CHECKS = List.of(
    "AddNullMarkedToPackageInfo",
    "AmbiguousMethodReference",
    "ArgumentSelectionDefectChecker",
    "ArrayAsKeyOfSetOrMap",
    "AssertEqualsArgumentOrderChecker",
    "AssertFalse",
    "AssertThrowsMultipleStatements",
    "AssertionFailureIgnored",
    "BadComparable",
    "BadImport",
    "BadInstanceof",
    "BigDecimalEquals",
    "BigDecimalLiteralDouble",
    "BoxedPrimitiveConstructor",
    "BoxedPrimitiveEquality",
    "ByteBufferBackingArray",
    "CacheLoaderNull",
    "CannotMockFinalClass",
    "CanonicalDuration",
    "CatchAndPrintStackTrace",
    "CatchFail",
    "CheckedExceptionNotThrown",
    "ClassCanBeStatic",
    "ClassName",
    "ClassNamedLikeTypeParameter",
    "ClassNewInstance",
    "CollectionUndefinedEquality",
    "CollectorShouldNotUseState",
    "ComparableAndComparator",
    "CompareToZero",
    "ComparisonContractViolated",
    "ComplexBooleanConstant",
    "ConstantField",
    "DateFormatConstant",
    "DeduplicateConstants",
    "DefaultCharset",
    "DefaultPackage",
    "DoubleBraceInitialization",
    "DoubleCheckedLocking",
    "EmptyCatch",
    "EmptyIf",
    "EmptyTopLevelDeclaration",
    "EqualsBrokenForNull",
    "EqualsGetClass",
    "EqualsIncompatibleType",
    "EqualsUnsafeCast",
    "EqualsUsingHashCode",
    "ExpectedExceptionChecker",
    "ExtendingJUnitAssert",
    "FallThrough",
    "FieldCanBeLocal",
    "FieldCanBeStatic",
    "Finally",
    "FloatCast",
    "FloatingPointLiteralPrecision",
    "ForEachIterable",
    "FutureReturnValueIgnored",
    "FuzzyEqualsShouldNotBeUsedInEqualsMethod",
    "GetClassOnEnum",
    "HidingField",
    "ImmutableAnnotationChecker",
    "ImmutableEnumChecker",
    "InconsistentCapitalization",
    "InconsistentHashCode",
    "InconsistentOverloads",
    "IncrementInForLoopAndHeader",
    "InlineFormatString",
    "InputStreamSlowMultibyteRead",
    "InstanceOfAndCastMatchWrongType",
    "InterfaceWithOnlyStatics",
    "InterruptedExceptionSwallowed",
    "InvalidThrows",
    "IterableAndIterator",
    "IterablePathParameter",
    "JavaDurationGetSecondsGetNano",
    "JavaDurationWithNanos",
    "JavaDurationWithSeconds",
    "JavaInstantGetSecondsGetNano",
    "JavaLangClash",
    "JavaLocalDateTimeGetNano",
    "JavaLocalTimeGetNano",
    "LockNotBeforeTry",
    "LockOnBoxedPrimitive",
    "LogicalAssignment",
    "LongLiteralLowerCaseSuffix",
    "MethodCanBeStatic",
    "MissingCasesInEnumSwitch",
    "MissingOverride",
    "MixedMutabilityReturnType",
    "ModifiedButNotUsed",
    "ModifyCollectionInEnhancedForLoop",
    "ModifySourceCollectionInStream",
    "MultiVariableDeclaration",
    "MultipleParallelOrSequentialCalls",
    "MultipleTopLevelClasses",
    "MultipleUnaryOperatorsInMethodCall",
    "MutableConstantField",
    "MutablePublicArray",
    "NestedInstanceOfConditions",
    "NonAtomicVolatileUpdate",
    "NonCanonicalStaticMemberImport",
    "NonOverridingEquals",
    "NullOptional",
    "NullableConstructor",
    "NullablePrimitive",
    "NullableVoid",
    "NumericEquality",
    "ObjectToString",
    "ObjectsHashCodePrimitive",
    "OperatorPrecedence",
    "OptionalMapToOptional",
    "OrphanedFormatString",
    "Overrides",
    "OverrideThrowableToString",
    "PackageLocation",
    "PreconditionsCheckNotNullRepeated",
    "PreferJavaTimeOverload",
    "PrimitiveAtomicReference",
    "ProtectedMembersInFinalClass",
    "ReferenceEquality",
    "RemoveUnusedImports",
    "ReturnFromVoid",
    "RxReturnValueIgnored",
    "SameNameButDifferent",
    "ShortCircuitBoolean",
    "StaticAssignmentInConstructor",
    "StaticGuardedByInstance",
    "StaticQualifiedUsingExpression",
    "StreamResourceLeak",
    "StringSplitter",
    "SynchronizeOnNonFinalField",
    "ThreadJoinLoop",
    "ThreadLocalUsage",
    "ThreeLetterTimeZoneID",
    "TimeUnitConversionChecker",
    "ToStringReturnsNull",
    "TreeToString",
    "TypeEquals",
    "TypeNameShadowing",
    "TypeParameterShadowing",
    "TypeParameterUnusedInFormals",
    "URLEqualsHashCode",
    "UndefinedEquals",
    "UnnecessaryAnonymousClass",
    "UnnecessaryLambda",
    "UnnecessaryMethodInvocationMatcher",
    "UnnecessaryParentheses",
    "UnsafeFinalization",
    "UnsafeReflectiveConstructionCast",
    "UseCorrectAssertInTests",
    "Var",
    "VariableNameSameAsType",
    "WaitNotInLoop"
  );

  private static final List<String> IDEA_EXCLUDED_CHECKS = List.of(
    "WildcardImport",
    "UnusedVariable",
    "UnusedMethod",
    "UnusedNestedClass"
  );

  private static final List<String> PRODUCTION_CHECKS = List.of(JAVA_TIME_DEFAULT_TIME_ZONE, NULL_AWAY);

  /**
   * Default constructor.
   */
  public CompilePlugin() {}

  private static void configureNullAway(ErrorProneOptions epOptions) {
    var annotatedPackages = String.join(COMMA, ANNOTATED_PACKAGES);
    var unannotatedSubPackages = String.join(COMMA, UNANNOTATED_SUBPACKAGES);
    epOptions.option("NullAway:AcknowledgeRestrictiveAnnotations", true);
    epOptions.option("NullAway:AnnotatedPackages", annotatedPackages);
    epOptions.option("NullAway:CheckContracts", true);
    epOptions.option("NullAway:CheckOptionalEmptiness", true);
    epOptions.option("NullAway:ExcludedFieldAnnotations", "org.junit.jupiter.api.io.TempDir");
    epOptions.option("NullAway:HandleTestAssertionLibraries", true);
    epOptions.option("NullAway:JSpecifyMode", true);
    epOptions.option("NullAway:UnannotatedSubPackages", unannotatedSubPackages);
  }

  private static void configureCompilerArgs(JavaCompile task) {
    var options = task.getOptions();
    options.setEncoding("UTF-8");
    options
      .getCompilerArgs()
      .addAll(
        List.of(
          "-parameters",
          "-implicit:class",
          "-g",
          "-Xdiags:verbose",
          "-Xlint:all",
          "-Xlint:-processing",
          "-Xlint:-exports",
          "-Xlint:-requires-transitive-automatic",
          "-Xlint:-requires-automatic",
          "-Xlint:-fallthrough" // handled by error-prone in a smarter way
        )
      );
  }

  private static void configureErrorProne(JavaCompile task, boolean inIdea) {
    var optionsExtensions = ((ExtensionAware) task.getOptions()).getExtensions();
    var epOptions = optionsExtensions.findByType(ErrorProneOptions.class);
    if (epOptions == null) {
      return;
    }

    configureDisabledChecks(epOptions);
    configureNullAway(epOptions);
    configureErrorProneChecks(task, epOptions, inIdea);
  }

  private static void configureDisabledChecks(ErrorProneOptions epOptions) {
    epOptions.disable(
      "InvalidInlineTag", // https://github.com/google/error-prone/issues/4308
      "MultipleNullnessAnnotations" // https://github.com/google/error-prone/issues/4334
    );

    epOptions.getDisableWarningsInGeneratedCode().set(true);
    epOptions.getExcludedPaths().set(".*/build/generated/sources/annotationProcessor/.*");
  }

  private static void configureErrorProneChecks(JavaCompile task, ErrorProneOptions epOptions, boolean inIdea) {
    var errors = new ArrayList<>(BASE_ERROR_PRONE_CHECKS);

    if (!inIdea) {
      errors.addAll(IDEA_EXCLUDED_CHECKS);
    }

    var taskName = task.getName();

    if (isProductionOrTestFixtures(taskName)) {
      errors.addAll(PRODUCTION_CHECKS);
    }

    if (isTestCode(taskName)) {
      task.getOptions().getCompilerArgs().addAll(List.of("-Xlint:-unchecked", "-Xlint:-varargs"));
      epOptions.disable(JAVA_TIME_DEFAULT_TIME_ZONE);
      epOptions.disable(NULL_AWAY);
    }

    epOptions.error(errors.toArray(String[]::new));
  }

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(JavaPlugin.class);
    project.getPluginManager().apply(ErrorPronePlugin.class);

    var inIdea = project.getProviders().systemProperty("idea.active").map(Boolean::parseBoolean).getOrElse(false);

    var tasks = project.getTasks();
    tasks
      .withType(JavaCompile.class)
      .configureEach(task -> {
        configureCompilerArgs(task);
        configureErrorProne(task, inIdea);
      });

    tasks.register("compile", task -> {
      task.dependsOn(tasks.withType(JavaCompile.class));
      task.setGroup("Build");
      task.setDescription("Compiles all Java source files.");
    });
  }

  /**
   * Checks if the task compiles production code or test fixtures.
   *
   * <p>Test fixtures are artifacts consumed by other projects, so they should be treated like
   * production code.
   *
   * @param taskName the name of the compilation task
   * @return true if this is production or test fixtures compilation
   */
  static boolean isProductionOrTestFixtures(String taskName) {
    return COMPILE_JAVA.equals(taskName) || COMPILE_TEST_FIXTURES_JAVA.equals(taskName);
  }

  /**
   * Checks if the task compiles test code (but not test fixtures).
   *
   * @param taskName the name of the compilation task
   * @return true if this is test code compilation (excluding test fixtures)
   */
  static boolean isTestCode(String taskName) {
    return !COMPILE_TEST_FIXTURES_JAVA.equals(taskName) && taskName.startsWith(COMPILE_TEST_PREFIX);
  }
}
