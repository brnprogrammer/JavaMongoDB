package br.com.escola.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeoLocalizacao {

	@GetMapping("/geolocalizacao/iniciarpesquisa")
	public String inicializarPesquisa() {
		return "geolocalizacao/pesquisar";
	}

}
