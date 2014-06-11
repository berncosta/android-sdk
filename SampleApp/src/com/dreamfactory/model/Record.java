package com.dreamfactory.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
public class Record {
  /* Array of field name-value pairs. */
  @JsonProperty("_field_")
  private List<String> _field_ = new ArrayList<String>();

  @JsonProperty("id")
  private String id;
  
  @JsonProperty("data")
  private Date data;
  
  @JsonProperty("carteira")
  private String carteira;
  
  @JsonProperty("tipo")
  private String tipo;
  
  @JsonProperty("categoria")
  private String categoria;
  
  @JsonProperty("valor")
  private String valor;
  
  @JsonProperty("comentario")
  private String comentario;
  
  
  public List<String> get_field_() {
    return _field_;
  }
  public void set_field_(List<String> _field_) {
    this._field_ = _field_;
  }
  
  public void setId(String id){
	  this.id = id;
  }

  public String getData() {
	SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//	Date pData = new Date(0);
//	try {
//		pData = sdf2.parse(data);
//	} catch (ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	String sData = sdf1.format(data);
	return sData;
}
  public String getData(int i){
	  return this.getData().substring(0,i);
  }
public void setData(Date data) {
	this.data = data;
}
public String getCarteira() {
	return carteira;
}
public void setCarteira(String carteira) {
	this.carteira = carteira;
}
public String getTipo() {
	return tipo;
}
public void setTipo(String tipo) {
	this.tipo = tipo;
}
public String getCategoria() {
	return categoria;
}
public String getCategoria(int i){
	return categoria.substring(0,i)+".";
	
}

public void setCategoria(String categoria) {
	this.categoria = categoria;
}
public String getValor() {
	return valor;
}
public void setValor(String valor) {
	this.valor = valor;
}
public String getComentario() {
	return comentario;
}
public void setComentario(String comentario) {
	this.comentario = comentario;
}
public String getId(){
	  return id;
  }
  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Record {\n");
    sb.append("  _field_: ").append(_field_).append("\n");
    sb.append("  valor: ").append(valor).append("\n");
    sb.append("  id: ").append(id).append("\n");
    sb.append("  data: ").append(data).append("\n");
    sb.append("  tipo: ").append(tipo).append("\n");
    sb.append("  categoria: ").append(categoria).append("\n");
    sb.append("  carteira: ").append(carteira).append("\n");
    sb.append("  comentario: ").append(comentario).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

