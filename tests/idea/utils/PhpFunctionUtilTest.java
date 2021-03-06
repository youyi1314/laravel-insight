package net.rentalhost.idea.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import net.rentalhost.suite.FixtureSuite;

public class PhpFunctionUtilTest extends FixtureSuite {
    private static boolean hasOnlyTypes(
        @NotNull final PsiElement fileSample,
        @NotNull final String functionName,
        @NotNull final PhpType... expectedTypesList
    ) {
        final Collection<String> returnTypes = new ArrayList<>(valueOf(PhpFunctionUtil.getReturnType(getFunction(fileSample, functionName))).getTypes());

        for (final PhpType expectedTypes : expectedTypesList) {
            for (final String expectedType : expectedTypes.getTypes()) {
                if (!returnTypes.contains(expectedType)) {
                    return false;
                }

                returnTypes.remove(expectedType);
            }
        }

        return returnTypes.isEmpty();
    }

    @NotNull
    private static Function getFunction(
        @NotNull final PsiElement fileSample,
        @NotNull final String functionName
    ) {
        return (Function) getElementByName(fileSample, functionName);
    }

    public void testGetReturnType() {
        final PsiFile fileSample = getResourceFile("utils/PhpFunctionUtil.returnTypes.php");

        // Bogus test...
        Assert.assertFalse(hasOnlyTypes(fileSample, "respectPhpdocReturnType_StringOnly", PhpType.STRING, PhpType.NULL));

        // Tests...
        final PhpType typeUnresolvableQualifier = PhpType.builder().add("\\UnresolvableQualifier").build();
        final PhpType typeResolvableQualifier   = PhpType.builder().add("\\MyNamespace\\ResolvableQualifier").build();
        final PhpType typeABCQualifiers         = PhpType.builder().add("\\A").add("\\B").add("\\C").build();
        final PhpType typeThisQualifier         = PhpType.builder().add("\\ThisQualifier").build();
        final PhpType typeSelfQualifier         = PhpType.builder().add("\\SelfQualifier").build();
        final PhpType typeChainSimulator        = PhpType.builder().add("\\ChainSimulator").build();

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectPhpdocReturnType_StringOnly", PhpType.STRING));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectPhpdocReturnType_StringOrNull", PhpType.STRING, PhpType.NULL));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectPhpdocReturnType_NullOrString", PhpType.STRING, PhpType.NULL));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectPhpdocReturnType_AllScalarTypes", PhpType.SCALAR));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectPhpdocReturnType_UnresolvableQualifier", typeUnresolvableQualifier));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectPhpdocReturnType_ResolvableQualifier", typeResolvableQualifier));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectReturnType_SingularType", PhpType.INT));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectReturnType_SingularNullableType", PhpType.INT, PhpType.NULL));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectReturnType_UnresolvableQualifierType", typeUnresolvableQualifier));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectReturnType_UnresolvableQualifierNullableType", typeUnresolvableQualifier, PhpType.NULL));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectReturnType_CCShouldIgnoresPhpdoc", PhpType.INT));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectReturnType_ResolvableQualifier", typeResolvableQualifier));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectNewReturnType_UnresolvableQualifier", typeUnresolvableQualifier));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectNewReturnType_ResolvableQualifier", typeResolvableQualifier));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectNewReturnType_ABCQualifiers", typeABCQualifiers));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectClosureReturnType", PhpType.CLOSURE));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectClosureReturnType_shouldIgnoresInnerReturnType", PhpType.CLOSURE));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectIndirectReturnType", typeResolvableQualifier));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectVariableTypeOnReturn", PhpType.STRING));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectThisTypeOnReturn", typeThisQualifier));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectSelfQualifierOnReturn", typeSelfQualifier));

        Assert.assertTrue(hasOnlyTypes(fileSample, "unifyDuplicatedReturnTypes", typeResolvableQualifier));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectChainedReturnType_singleCall_fromNew", typeChainSimulator));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectChainedReturnType_multiCall_fromVariable", typeChainSimulator));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectMixedReturnType_fromUnresolvableReference_fromVariable", PhpType.MIXED));
        Assert.assertTrue(hasOnlyTypes(fileSample, "respectMixedReturnType_fromUnresolvableReference_fromFunction", PhpType.MIXED));

        Assert.assertTrue(hasOnlyTypes(fileSample, "avoidInfinityLoopingA_mixedType", PhpType.MIXED));
        Assert.assertTrue(hasOnlyTypes(fileSample, "avoidInfinityLoopingA_respectPhpdoc", PhpType.STRING));

        Assert.assertTrue(hasOnlyTypes(fileSample, "respectComplexReturnType", typeChainSimulator));
    }
}
