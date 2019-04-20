package br.com.escola.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import br.com.escola.codec.AlunoCodec;
import br.com.escola.model.Aluno;

@Repository
public class AlunoRepository {

	private MongoClient client;
	private MongoDatabase db;


	private void conexao(){
		Codec<Document> codec = MongoClient.getDefaultCodecRegistry().get(Document.class);

		AlunoCodec alunoCodec = new AlunoCodec(codec);

		CodecRegistry registro = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(alunoCodec));

		MongoClientOptions opcoes = MongoClientOptions.builder().codecRegistry(registro).build();

		this.client = new MongoClient("localhost:27017", opcoes);
		this.db = client.getDatabase("test");
	}
	
	public void salvar(Aluno aluno) {
		conexao();
		
		MongoCollection<Aluno> alunos = db.getCollection("alunos", Aluno.class);
		if(aluno.getId() == null) {
			alunos.insertOne(aluno);
		}else {
			alunos.updateOne(Filters.eq("_id", aluno.getId()), new Document("$set", aluno));
		}
		
		fecharConexao();
	}


	public List<Aluno> obterAlunos() {
		conexao();
		MongoDatabase db = client.getDatabase("test");
		MongoCollection<Aluno> alunos = db.getCollection("alunos", Aluno.class);

		MongoCursor<Aluno> resultados = alunos.find().iterator();

		List<Aluno> alunosEncontrados = popularAluno(resultados);

		fecharConexao();

		return alunosEncontrados;
	}
	
	public Aluno alunoPorId(String id){
		conexao();
		MongoCollection<Aluno> alunos = this.db.getCollection("alunos", Aluno.class);
		Aluno aluno = alunos.find(Filters.eq("_id", new ObjectId(id))).first();
		return aluno;
	}

	public List<Aluno> pesquisarPor(String nome) {
		conexao();
		MongoCollection<Aluno> alunoCollection = this.db.getCollection("alunos", Aluno.class);
		MongoCursor<Aluno> resultados = alunoCollection.find(Filters.eq("nome",nome), Aluno.class).iterator();
		List<Aluno> alunos = popularAluno(resultados);
		fecharConexao();
		
		return alunos;
	}

	private void fecharConexao() {
		this.client.close();
	}

	private List<Aluno> popularAluno(MongoCursor<Aluno> resultados){
		List<Aluno> alunos = new ArrayList<>();
		while(resultados.hasNext()) {
			alunos.add(resultados.next());
		}
		return alunos;
	}

	public List<Aluno> pesquisarPor(String classificacao, double nota) {
		conexao();
		
		MongoCollection<Aluno> alunoCollection = this.db.getCollection("alunos", Aluno.class);
		
		MongoCursor<Aluno> resultados = null;
		if(classificacao.equals("reprovado")) {
			resultados = alunoCollection.find(Filters.lt("notas", nota)).iterator();
		}else if(classificacao.equals("aprovados")) {
			resultados = alunoCollection.find(Filters.gte("notas", nota)).iterator();
		}
		
		List<Aluno> alunos = popularAluno(resultados);
		
		fecharConexao();
		
		return alunos;	
	}
	
}
