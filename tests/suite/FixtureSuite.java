package net.rentalhost.suite;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;

import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FixtureSuite extends CodeInsightFixtureTestCase {
    private static String getResourcesPath() {
        return Paths.get("resources-tests").toAbsolutePath().toString();
    }

    @NotNull
    protected static <T> T valueOf(@Nullable final T element) {
        assert element != null;

        return element;
    }

    protected FixtureChain inspectTool(final Class<? extends LocalInspectionTool> inspectionTool) {
        return new FixtureChain(myFixture).addInspectionTool(inspectionTool);
    }

    @Nullable
    protected PsiFile getResourceFile(final String path) {
        final String      pathAbsolute = Paths.get("resources-tests/" + path).toAbsolutePath().toString();
        final VirtualFile virtualFile  = LocalFileSystem.getInstance().findFileByPath(pathAbsolute);

        assert virtualFile != null;

        return PsiManager.getInstance(myFixture.getProject()).findFile(virtualFile);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        VfsRootAccess.allowRootAccess(getResourcesPath());
    }

    @Override
    protected void tearDown() throws Exception {
        VfsRootAccess.disallowRootAccess(getResourcesPath());

        super.tearDown();
    }
}
