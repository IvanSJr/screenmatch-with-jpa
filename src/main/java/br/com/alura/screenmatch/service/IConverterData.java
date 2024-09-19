package br.com.alura.screenmatch.service;

public interface IConverterData {
    <T> T getSeriesData(String json, Class<T> classe);
}
