package org.rebecalang.modelchecker.setting;

import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;

import java.util.Set;

public interface ModelCheckerSetting {
    void setCompilerExtension(Set<CompilerExtension> extension);
    Set<CompilerExtension> getCompilerExtension();

    CoreVersion getCoreVersion();
    void setCoreVersion(CoreVersion coreVersion);

    Policy getPolicy();
    void setPolicy(Policy policy);
}