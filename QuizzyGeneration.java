package network;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuizzyGeneration {
   public static String prompt(int numQuestions, String topic, String difficulty){ 
    String prompt = String.format("Give me %d multiple choice questions about %s. The questions should be at an %s level. Return your answer entirely in the form of a JSON object. The JSON object should have a key named 'questions' which is an array of the questions. Each quiz question should include the choices, the answer, and a brief explanation of why the answer is correct. Don't include anything other than the JSON. The JSON properties of each question should be 'query' (which is the question), 'choices', 'answer', and 'explanation'. The choices shouldn't have any ordinal value like A, B, C, D or a number like 1, 2, 3, 4. The answer should be the 0-indexed number of the correct choice.", numQuestions, topic, difficulty);
    
    // return string with this format: 
    // {
    //     "questions": [
    //       {
    //         "query": "What is the capital city of Vietnam?",
    //         "choices": ["Ho Chi Minh City", "Da Nang", "Hanoi", "Hue"],
    //         "answer": 2,
    //         "explanation": "Hanoi is the capital city of Vietnam, serving as the political center of the country."
    //       },
    //      ]}

    return prompt;
   }

   public static String chatGPT(String prompt, String apiKey) {
       String url = "https://api.openai.com/v1/chat/completions";
       String model = "gpt-3.5-turbo";

       try {
           URL obj = new URL(url);
           HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
           connection.setRequestMethod("POST");
           connection.setRequestProperty("Authorization", "Bearer " + apiKey);
           connection.setRequestProperty("Content-Type", "application/json");

           // The request body
           String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
           connection.setDoOutput(true);
           OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
           writer.write(body);
           writer.flush();
           writer.close();

           // Response from ChatGPT
           BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
           String line;

           StringBuffer response = new StringBuffer();

           while ((line = br.readLine()) != null) {
               response.append(line);
           }
           br.close();

           // calls the method to extract the message.
           return extractMessageFromJSONResponse(response.toString());

       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }

   public static String extractMessageFromJSONResponse(String response) {
       int start = response.indexOf("content")+ 11;

       int end = response.indexOf("\"", start);

       return response.substring(start, end);

   }

   public static void getQuiz(String topic, int numQuestions, String difficulty, String apiKey){
     String prompt = prompt(numQuestions, topic, difficulty);
     String quiz = chatGPT(prompt, apiKey);
     String fileName = "quiz.json";

     try (FileWriter fileWriter = new FileWriter(fileName)) {
        fileWriter.write(quiz);
        fileWriter.flush();
        System.out.println("Successfully wrote quiz to file: " + fileName);
    } catch (IOException e) {
        System.out.println("An error occurred while writing quiz to file.");
        e.printStackTrace();
    }
   }
}

