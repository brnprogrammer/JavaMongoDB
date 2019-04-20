package br.com.escola.codec;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import br.com.escola.model.Aluno;
import br.com.escola.model.Curso;
import br.com.escola.model.Habilidade;
import br.com.escola.model.Nota;

public class AlunoCodec implements CollectibleCodec<Aluno> {

	private Codec<Document> codec;

	public AlunoCodec(Codec<Document> codec) {
		this.codec = codec;
	}

	@Override
	public void encode(BsonWriter writer, Aluno aluno, EncoderContext encoder) {
		//passando objeto java para o mongo
		ObjectId id = aluno.getId();
		String nome = aluno.getNome();
		Date dataNascimento = aluno.getDataNascimento();
		Curso curso = aluno.getCurso();
		List<Habilidade> habilidades = aluno.getHabilidades();
		List<Nota> notas = aluno.getNotas();

		Document document = new Document();
		document.put("_id", id);
		document.put("nome", nome);
		document.put("data_nascimento", dataNascimento);
		document.put("curso", new Document("nome", curso.getNome())); // objeto json, por isso um new Document ao curso
		
		if(habilidades != null) {
			List<Document> habilidadesDocument = new ArrayList<>();
			
			for (Habilidade habilidade : habilidades) {
				habilidadesDocument.add(new Document("nome", habilidade.getNome()).append("nivel", habilidade.getNivel()));
			}
			
			document.put("habilidades", habilidadesDocument);
		}
		
		if(notas != null) {
			List<Double> notasAluno = new ArrayList<>();
			for (Nota nota : notas) {
				notasAluno.add(nota.getValor());
			}
			document.put("notas", notasAluno);
		}
		
		codec.encode(writer, document, encoder);

	}

	@Override
	public Class<Aluno> getEncoderClass() {
		// TODO Auto-generated method stub
		return Aluno.class;
	}

	@Override
	public Aluno decode(BsonReader reader, DecoderContext decoder) {
		//pegando o document do mongo e transformando em objeto java
		Document document = codec.decode(reader, decoder);
		Aluno aluno = new Aluno();
		aluno.setId(document.getObjectId("_id"));
		aluno.setNome(document.getString("nome"));
		aluno.setDataNscimento(document.getDate("data_nascimento"));
		Document curso = (Document) document.get("curso");
		if(curso != null) {
			String nomeCurso = curso.getString("nome");
			aluno.setCurso(new Curso(nomeCurso));
		}
		
		
		List<Double> notas = (List<Double>) document.get("notas");
		if(notas != null) {
			List<Nota> notasAluno = new ArrayList<>();
			for (Double nota : notas) {
				notasAluno.add(new Nota(nota));
			}
			aluno.setNotas(notasAluno);
		}
		
		
		List<Document> habilidades = (List<Document>) document.get("habilidades");
		
		if(habilidades != null) {
			List<Habilidade> habilidadeAluno = new ArrayList<>();
			for (Document documentHabilidade : habilidades) {
				habilidadeAluno.add(new Habilidade(documentHabilidade.getString("nome"), documentHabilidade.getString("nivel")));
			}
			aluno.setHabilidades(habilidadeAluno);
		}
		
		return aluno;
	}

	@Override
	public Aluno generateIdIfAbsentFromDocument(Aluno aluno) {

		return documentHasId(aluno) ? aluno.criarId() : aluno;
	}

	@Override
	public boolean documentHasId(Aluno aluno) {
		return aluno.getId() == null;
	}

	@Override
	public BsonValue getDocumentId(Aluno aluno) {
		if (!documentHasId(aluno)) {
			throw new IllegalStateException("Documento sem id !");
		}
		return new BsonString(aluno.getId().toHexString()); // id em base hexa
	}

}
