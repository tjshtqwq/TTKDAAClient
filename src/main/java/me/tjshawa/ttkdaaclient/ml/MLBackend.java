package me.tjshawa.ttkdaaclient.ml;

import java.util.List;

public interface MLBackend {
    AimbotResponse predictAimML(List<Double> data);
}
