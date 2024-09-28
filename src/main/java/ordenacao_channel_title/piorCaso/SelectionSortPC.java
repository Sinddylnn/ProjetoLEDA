package main.java.ordenacao_channel_title.piorCaso;

import org.apache.commons.csv.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SelectionSortPC {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Marca o tempo de início da execução

        // Define os caminhos para os arquivos de entrada e saída
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_selectionSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_selectionSort_piorCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title"

        // Início do processamento
        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return; // Finaliza a execução se o arquivo não for encontrado
        }

        // Lê os dados do arquivo CSV
        String[][] data = readCsv(inputPath);

        // Verifica se os dados foram lidos corretamente
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex); // Limpa espaços em branco na coluna especificada

            // Exibe os valores da coluna antes da ordenação
            System.out.println("Valores da coluna channel_title antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][columnIndex]);
            }

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data); // Preenche linhas que têm menos colunas do que o cabeçalho

            // Verifica se os dados já estão ordenados em ordem decrescente
            if (isAlreadySorted(data, columnIndex, false)) {
                System.out.println("Os dados já estão ordenados em ordem decrescente. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação por Selection Sort...");
                try {
                    sortByChannelTitle(data, columnIndex); // Ordena os dados usando Selection Sort
                    System.out.println("Ordenação por Selection Sort finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace(); // Exibe a pilha de chamadas em caso de erro
                }
            }

            // Grava os dados ordenados no arquivo de saída
            writeCsv(data, outputPath);
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis(); // Marca o tempo de fim da execução
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory(); // Calcula a memória utilizada

        // Exibe o tempo de execução e a memória utilizada
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Ordenação por Selection Sort em ordem decrescente
    public static void sortByChannelTitle(String[][] data, int columnIndex) {
        // Verifica se os dados estão adequados para ordenação
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return; // Sai da função se não houver dados
        }

        // Verifica se o índice da coluna é válido
        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return; // Sai da função se o índice for inválido
        }

        // Implementa o algoritmo Selection Sort
        for (int i = 1; i < data.length - 1; i++) {
            int maxIndex = i; // Assume que o primeiro elemento é o maior

            // Procura o maior valor a partir do índice atual
            for (int j = i + 1; j < data.length; j++) {
                if (data[j][columnIndex].compareToIgnoreCase(data[maxIndex][columnIndex]) > 0) {
                    maxIndex = j; // Atualiza o índice do maior valor encontrado
                }
            }

            // Troca os elementos se um maior foi encontrado
            if (maxIndex != i) {
                String[] temp = data[i];
                data[i] = data[maxIndex];
                data[maxIndex] = temp;
            }

            // Envia mensagem a cada 1000 linhas ordenadas
            if (i % 1000 == 0) {
                System.out.println("Ordenadas até a linha: " + i);
            }
        }
        System.out.println("Ordenação por Selection Sort finalizada.");
    }

    // Lê os dados de um arquivo CSV e retorna um array bidimensional
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][]; // Inicializa o array de dados
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a primeira linha como cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Separa os cabeçalhos
            data = new String[1][headers.length]; // Inicializa o array com o cabeçalho
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            // Configura o formato do CSV
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat); // Cria um parser CSV
            int rowCount = 1; // Contador de linhas
            for (CSVRecord record : csvParser) {
                // Ignora linhas que não têm colunas suficientes
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande o array para incluir a nova linha
                data[rowCount] = record.stream().toArray(String[]::new); // Adiciona a linha lida
                rowCount++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace(); // Exibe a pilha de chamadas em caso de erro
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        }
        return data; // Retorna os dados lidos
    }

    // Remove espaços em branco da coluna especificada
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Preenche as linhas que têm menos colunas do que o cabeçalho
    public static void fillMissingColumns(String[][] data) {
        int maxColumns = Arrays.stream(data).mapToInt(row -> row.length).max().orElse(0); // Determina o número máximo de colunas
        for (int i = 0; i < data.length; i++) {
            if (data[i].length < maxColumns) {
                data[i] = Arrays.copyOf(data[i], maxColumns); // Preenche colunas ausentes
            }
        }
    }

    // Verifica se os dados na coluna estão ordenados
    public static boolean isAlreadySorted(String[][] data, int columnIndex, boolean ascending) {
        for (int i = 1; i < data.length - 1; i++) {
            if (ascending) {
                if (data[i][columnIndex].compareToIgnoreCase(data[i + 1][columnIndex]) > 0) {
                    return false; // Não está ordenado em ordem crescente
                }
            } else {
                if (data[i][columnIndex].compareToIgnoreCase(data[i + 1][columnIndex]) < 0) {
                    return false; // Não está ordenado em ordem decrescente
                }
            }
        }
        return true; // Os dados estão ordenados
    }

    // Grava os dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            for (String[] row : data) {
                writer.write(String.join(",", row) + "\n"); // Escreve cada linha no arquivo
            }
            System.out.println("Dados gravados em: " + filePath.toString());
        } catch (IOException e) {
            System.err.println("Erro ao gravar o arquivo: " + e.getMessage());
            e.printStackTrace(); // Exibe a pilha de chamadas em caso de erro
        }
    }

    // Expande um array bidimensional para incluir mais linhas
    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][]; // Cria um novo array com o tamanho desejado
        System.arraycopy(original, 0, newArray, 0, original.length); // Copia o conteúdo do original
        return newArray; // Retorna o novo array expandido
    }
}
