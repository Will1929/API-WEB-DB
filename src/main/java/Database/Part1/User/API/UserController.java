package Database.Part1.User.API;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.swagger.annotations.ApiImplicitParam;

@Controller
@EnableWebMvc
public class UserController {

	public static Gson gson = new Gson();

	@CrossOrigin(origins = "*")
	@GetMapping("/form")
	public String form(Model model) {

		User user = new User();

		model.addAttribute("user", user);

		return "form";
	}

	@PostMapping("/createUser")
	public String createUser(@ModelAttribute("user") User user) {

		try {

			String jsonExist = "";
			File jsonFile = new File("JsonFile/allUsers");
			if (jsonFile.exists()) {
				jsonExist = Files.readString(jsonFile.toPath());
			}

			JsonArray jsonArray = new JsonArray();
			if (!jsonExist.isEmpty()) {
				jsonArray = JsonParser.parseString(jsonExist).getAsJsonArray();
			}

			JsonObject newUserJson = gson.toJsonTree(user).getAsJsonObject();
			jsonArray.add(newUserJson);

			BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));

			writer.write(jsonArray.toString());
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/success";

	}

	@GetMapping("/success")
	public String success() {
		return "success";
	}

	@GetMapping(value = "/checkUser/{cpf}", produces = "application/json")
	@ResponseBody
	@ApiImplicitParam(name = "CPF", dataType = "Integer")
	public String checkUser(@PathVariable String cpf) {
		try {
			FileReader reader = new FileReader("JsonFile/allUsers");
			List<User> users = Arrays.asList(gson.fromJson(reader, User[].class));
			User userFound = users.stream().filter(usuario -> usuario.getCPF().equals(cpf)).findFirst().orElse(null);
			if (userFound != null) {
				return gson.toJson(userFound);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{}";
	}

}
