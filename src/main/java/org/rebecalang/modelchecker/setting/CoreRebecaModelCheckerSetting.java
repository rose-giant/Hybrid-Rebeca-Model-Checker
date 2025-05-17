package org.rebecalang.modelchecker.setting;

import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;

import java.util.HashSet;
import java.util.Set;

public class CoreRebecaModelCheckerSetting implements ModelCheckerSetting {
    private Set<CompilerExtension> compilerExtension = new HashSet<CompilerExtension>();
    private CoreVersion coreVersion;
    private Policy policy;

    public CoreRebecaModelCheckerSetting() {
    }

    public CoreRebecaModelCheckerSetting(Set<CompilerExtension> extension, CoreVersion coreVersion, Policy policy) {
        setCompilerExtension(extension);
        setCoreVersion(coreVersion);
        setPolicy(policy);
    }

    public void setCompilerExtension(Set<CompilerExtension> extension) {
        this.compilerExtension = extension;
    }

    public Set<CompilerExtension> getCompilerExtension() {
        return compilerExtension;
    }

    public CoreVersion getCoreVersion() {
        return coreVersion;
    }

    public void setCoreVersion(CoreVersion coreVersion) {
        this.coreVersion = coreVersion;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
}
