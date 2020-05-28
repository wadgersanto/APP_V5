package gnotus.inoveplastika;

public class JSONconverter {

    public JSONconverter() {
    }

    public String ConvertJSON(String inputJSON) {

        inputJSON = inputJSON.replaceAll("\\\\u000d", "");
        inputJSON = inputJSON.replaceAll("\\\\u000a", "\n");
        inputJSON = "[" + inputJSON.substring(2, (inputJSON.length() - 2)) + "]";
        inputJSON = inputJSON.replaceAll("\\\\", "");

        return inputJSON;
    }
}
