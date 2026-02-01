package org.rebecalang.transparentactormodelchecker.hybridrebeca.utils;

public class TimeSyncHelper {

    public Float Up(float t_2, float t_3, float t_4) {
        if (t_2 == t_3) return t_4;
        if (t_2 < t_3) return t_3;

        else return null;
    }
}
