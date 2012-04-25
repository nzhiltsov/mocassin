package ru.ksu.niimm.cll.mocassin.frontend.viewer.client.protovis;

public interface PVNodeAdapter<T> {

    String getNodeName(T t);

    Object getNodeValue(T t);

}