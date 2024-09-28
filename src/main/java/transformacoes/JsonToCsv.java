package main.java.transformacoes;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonToCsv {

    // Função para carregar e filtrar dados dos arquivos JSON
    public static String[][] loadAndFilterJson(String[] jsonFiles, String[] desiredTitles) {
        String[][] filteredData = new String[1000][];
        int filteredCount = 0;

        for (String jsonFile : jsonFiles) {
            File file = new File(jsonFile);
            if (file.exists()) {
                try {
                    String jsonContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(jsonContent);
                    JSONArray itemsArray = jsonObject.getJSONArray("items");

                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        String title = item.getJSONObject("snippet").getString("title").trim();

                        for (String desiredTitle : desiredTitles) {
                            if (title.equals(desiredTitle)) {
                                // Extrair todos os campos do snippet
                                JSONArray keys = item.getJSONObject("snippet").names();
                                String[] row = new String[keys.length()];
                                for (int j = 0; j < keys.length(); j++) {
                                    String key = keys.getString(j);
                                    row[j] = item.getJSONObject("snippet").getString(key);
                                }
                                filteredData[filteredCount++] = row;
                            }
                        }
                    }
                    System.out.println("Arquivo JSON '" + jsonFile + "' processado com sucesso.");
                } catch (IOException e) {
                    System.err.println("Erro ao processar o arquivo JSON '" + jsonFile + "': " + e.getMessage());
                }
            } else {
                System.out.println("Arquivo JSON '" + jsonFile + "' não encontrado.");
            }
        }
        // Retornar apenas as linhas preenchidas
        String[][] result = new String[filteredCount][];
        System.arraycopy(filteredData, 0, result, 0, filteredCount);
        return result;
    }

    // Função para escrever os dados filtrados em um arquivo CSV
    public static void writeToCsv(String outputCsv, String[][] filteredData) {
        if (filteredData.length > 0) {
            try (FileWriter out = new FileWriter(outputCsv);
                 CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {

                // Escreve o cabeçalho usando os primeiros campos
                for (String field : filteredData[0]) {
                    printer.print(field);
                }
                printer.println();

                // Escreve os dados
                for (String[] row : filteredData) {
                    printer.printRecord((Object[]) row);
                }

                System.out.println("Arquivo CSV '" + outputCsv + "' gerado com sucesso.");
            } catch (IOException e) {
                System.err.println("Erro ao escrever o arquivo CSV: " + e.getMessage());
            }
        } else {
            System.out.println("Nenhum dado para escrever no CSV.");
        }
    }

    public static void main(String[] args) {
        // Caminho para os arquivos JSON
        String[] jsonFiles = {
                "CA_category_id.json",
                "DE_category_id.json",
                "FR_category_id.json",
                "GB_category_id.json",
                "IN_category_id.json",
                "JP_category_id.json",
                "KR_category_id.json",
                "MX_category_id.json",
                "RU_category_id.json",
                "US_category_id.json"
        };

        // Títulos desejados
        String[] desiredTitles = {"Trailers", "Shows", "Shorts"};

        // Caminho para o arquivo CSV de saída
        String outputCsvFile = "videos_TSS.csv";

        // Iniciar processamento
        System.out.println("Iniciando processamento dos arquivos JSON...");
        String[][] filteredData = loadAndFilterJson(jsonFiles, desiredTitles);

        System.out.println("Gerando o arquivo CSV...");
        writeToCsv(outputCsvFile, filteredData);

        System.out.println("Processo concluído.");
    }
}
