package br.com.escola.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.escola.model.Aluno;
import br.com.escola.model.Habilidade;
import br.com.escola.repository.AlunoRepository;

@Controller
public class HabilidadeController {

	@Autowired
	private AlunoRepository repository;
	
	@GetMapping("/habilidade/cadastrar/{id}") // pega o id  do aluno para enviar para outra tela e adicionar skill
	public String cadastrar(@PathVariable String id, Model model) {
		Aluno aluno = repository.alunoPorId(id);
		model.addAttribute("aluno", aluno);
		model.addAttribute("habilidade", new Habilidade());
		return "habilidade/cadastrar";
	}
	
	
	@PostMapping("habilidade/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Habilidade habilidade) {
		Aluno aluno = repository.alunoPorId(id);
		repository.salvar(aluno.adicionar(aluno,habilidade));
		
		return "redirect:/aluno/listar";
	}
}
