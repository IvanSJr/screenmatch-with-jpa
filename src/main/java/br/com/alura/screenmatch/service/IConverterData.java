package br.com.alura.screenmatch.service;

public interface IConverterData {
    <T> T  obterDados(String json, Class<T> classe);
}
