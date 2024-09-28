package main.java.ordenacao_comment_count.medioCaso;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CountingSort2 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_countingSort_medioCaso.csv");
        int columnIndex = 11; // Índice da coluna "comment_count"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex);

            System.out.println("Valores da coluna comment_count antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][columnIndex]);
            }

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data);

            // Verifica se já está ordenado
            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação por Counting Sort...");
                try {
                    sortByCommentCountCounting(data, columnIndex);
                    System.out.println("Ordenação por Counting Sort finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            writeCsv(data, outputPath);
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    public static void sortByCommentCountCounting(String[][] data, int columnIndex) {
        System.out.println("Executando Counting Sort na coluna: " + columnIndex);
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return;
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return;
        }

        // Contando a ocorrência de cada valor de comment_count
        int maxCount = Integer.MIN_VALUE;
        int minCount = Integer.MAX_VALUE;

        // Primeira passagem para encontrar o min e max, ignorando entradas inválidas
        for (int i = 1; i < data.length; i++) {
            try {
                int count = Integer.parseInt(data[i][columnIndex].trim());
                if (count > maxCount) {
                    maxCount = count;
                }
                if (count < minCount) {
                    minCount = count;
                }
            } catch (NumberFormatException e) {
                System.err.println("Valor não numérico encontrado na linha " + i + ": " + data[i][columnIndex]);
                // Você pode decidir ignorar essas linhas ou tratá-las de alguma forma
            }
        }

        // Verifique se minCount foi atualizado. Se não, não há dados numéricos válidos.
        if (maxCount == Integer.MIN_VALUE) {
            System.err.println("Nenhum valor numérico válido encontrado na coluna " + columnIndex);
            return;
        }

        int range = maxCount - minCount + 1;
        int[] countArray = new int[range];

        // Contando as ocorrências, ignorando entradas inválidas
        for (int i = 1; i < data.length; i++) {
            try {
                int count = Integer.parseInt(data[i][columnIndex].trim());
                countArray[count - minCount]++;
            } catch (NumberFormatException e) {
                System.err.println("Valor não numérico encontrado na linha " + i + ": " + data[i][columnIndex]);
            }
        }

        // Construindo o array ordenado
        String[][] sortedData = new String[data.length][data[0].length];
        sortedData[0] = data[0]; // copia o cabeçalho

        int index = 1;
        for (int i = 0; i < countArray.length; i++) {
            while (countArray[i] > 0) {
                for (int j = 1; j < data.length; j++) {
                    try {
                        if (Integer.parseInt(data[j][columnIndex].trim()) == i + minCount) {
                            sortedData[index++] = data[j];
                            countArray[i]--;
                            break; // Para evitar adicionar o mesmo item novamente
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Valor não numérico encontrado na linha " + j + ": " + data[j][columnIndex]);
                    }
                }
            }
        }

        // Copia sortedData de volta para data
        System.arraycopy(sortedData, 0, data, 0, sortedData.length);
        System.out.println("Ordenação concluída. Dados ordenados:");
        for (int i = 1; i < data.length; i++) {
            System.out.println(data[i][columnIndex]);
        }
    }

    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(",");
            data = new String[1][headers.length];
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT.withTrim().withQuoteMode(QuoteMode.ALL);
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1);
                data[rowCount] = record.stream().toArray(String[]::new);
                System.out.println("Linha " + rowCount + " lida: " + Arrays.toString(data[rowCount]));
                rowCount++;
            }
            System.out.println("Total de linhas lidas: " + rowCount);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        }
        return data;
    }

    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            data[i][columnIndex] = data[i][columnIndex].trim();
        }
    }

    public static void fillMissingColumns(String[][] data) {
        int maxColumns = Arrays.stream(data).mapToInt(arr -> arr.length).max().orElse(0);
        for (int i = 0; i < data.length; i++) {
            if (data[i].length < maxColumns) {
                data[i] = Arrays.copyOf(data[i], maxColumns);
            }
        }
    }

    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            try {
                int current = Integer.parseInt(data[i][columnIndex].trim());
                int next = Integer.parseInt(data[i + 1][columnIndex].trim());
                if (current > next) {
                    return false; // Se encontrar uma inversão, não está ordenado
                }
            } catch (NumberFormatException e) {
                System.err.println("Valor não numérico encontrado na linha " + i + ": " + data[i][columnIndex]);
                // Se o valor não for numérico, podemos ignorar ou decidir que o arquivo não está ordenado
                // return false; // Você pode decidir interromper a verificação aqui ou continuar ignorando os valores não numéricos
            }
        }
        return true; // Está ordenado
    }


    public static void writeCsv(String[][] data, Path outputPath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.withHeader(data[0]))) {
            for (int i = 1; i < data.length; i++) {
                printer.printRecord((Object[]) data[i]);
            }
            System.out.println("Arquivo salvo em: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][original[0].length];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }
}
