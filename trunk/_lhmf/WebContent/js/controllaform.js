/*************************************************************/
/*   File:    controllaform.js				                 */
/*-----------------------------------------------------------*/
/*   Descrizione:  Questa file javascript permette di 	     */
/*                 verificare in tempo reale come l'utente	 */
/*                 sta compilando il form di registrazione e */
/*				   avvertirlo in caso di errori.			 */
/*************************************************************/

$(document).ready(function(){
	GestisciEventi();
});

/********************************************************************/
/*   Funzione si mette in ascolto di tutti gli oggetti che hanno	*/
/*	 come classe ".registrazioneform input.field" e fa scattare 	*/
/*	 una funzione ogni qualvolta si ha un focusout.	In seguito		*/
/*	 chiama una funzione che convalida o meno il campo che ha		*/
/*	 scatenato l'evento e gli modifica la classe in modo da 		*/
/*	 cambiare stile di visualizzazione								*/
/********************************************************************/

function GestisciEventi(){
	$(".registrazioneform input.field").bind("focusout",function(e){
	 if($(this).attr('name') != 'phone')
		if(controllaCampo($(this).attr('name')) == true){
			if($(this).attr('class') == 'field')
				$(this).removeClass('field');
			if($(this).attr('class') == 'field_notvalid')
				$(this).removeClass('field_notvalid');
			if($(this).attr('class') == 'field_valid')
				$(this).removeClass('field_valid');
				
			$(this).addClass('field_valid');
		} else{
			if($(this).attr('class') == 'field')
				$(this).removeClass('field');
			if($(this).attr('class') == 'field_notvalid')
				$(this).removeClass('field_notvalid');
			if($(this).attr('class') == 'field_valid')
				$(this).removeClass('field_valid');
				
            $(this).addClass('field_notvalid');
		}
	});
}

/********************************************************************/
/*   Funzione che verifica la validità del campo in esame.			*/
/********************************************************************/

function controllaCampo(name){
	var valore = get_valore(name);

	//alert('Attenzione! \nname: ' + name + ' \nvalore: ' + valore);
	
	switch(name) {
		case 'email':
			var email_reg_exp = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-]{2,})+\.)+([a-zA-Z0-9]{2,})+$/;
			if (!email_reg_exp.test(valore) || (valore == "") || (valore == "undefined")) 
				return false;
		break;
		case 'repassword':
			var regform = document.getElementById("regform");
			if ((valore == "") || (valore == "undefined") || (valore != regform.password.value)) 
				return false;
		break;
		case 'cap':
			if ((valore == "") || (valore == "undefined") || (isNaN(valore) == 1)) 
				return false;
		break;
		
		default:
			if ((valore == "") || (valore == "undefined")) 
				return false;
	}
	return true;
}

/********************************************************************/
/*   Funzione che restituisce il valore del campo passato come		*/
/* 	 ingresso.														*/
/********************************************************************/

function get_valore(name) {

var regform = document.getElementById("regform");

switch(name) {
		case 'firstname':
		valore = regform.firstname.value;
		break;
		case 'lastname':
		valore = regform.lastname.value;
		break;
		case 'address':
		valore = regform.address.value;
		break;
		case 'cap':
		valore = regform.cap.value;
		break;
		case 'city':
		valore = regform.city.value;
		break;
		case 'state':
		valore = regform.state.value;
		break;
		case 'email':
		valore = regform.email.value;
		break;
		case 'username':
		valore = regform.username.value;
		break;
		case 'password':
		valore = regform.password.value;
		break;
		case 'repassword':
		valore = regform.repassword.value;
		break;
	}
	return valore;
}


/********************************************************************/
/*   Funzione che permette di scrivere nella texarea adatta il 		*/
/* 	 messaggio di aiuto												*/
/********************************************************************/

function scrivi_help(campo) {
	
var regform = document.getElementById("regform");

	switch(campo) {
		case 'firstname':
		regform.helptext.value = 'Inserisci il tuo nome.';
		break;
		case 'lastname':
		regform.helptext.value = 'Inserisci il tuo cognome.';
		break;
		case 'address':
		regform.helptext.value = 'Inserisci l\'indirizzo (compreso di numero civico). \n NB: Quello di Fatturazione';
		break;
		case 'cap':
		regform.helptext.value = 'Inserisci il Codice di avviamento postale \n (sono accettati solo numeri)';
		break;
		case 'city':
		regform.helptext.value = 'Inserisci la tua città';
		break;
		case 'state':
		regform.helptext.value = 'Inserisci la Nazione';
		break;
		case 'email':
		regform.helptext.value = 'Inserisci una tua e-mail Valida \n (servirà per completare la tua registrazione)';
		break;
		case 'phone':
		regform.helptext.value = 'Inserisci il Telefono (opzionale)';
		break;
		case 'username':
		regform.helptext.value = 'Inserisci l\'username \n (ti servirà per accedere al sito)';
		break;
		case 'password':
		regform.helptext.value = 'Inserisci la Password';
		break;
		case 'repassword':
		regform.helptext.value = 'Riscrivi la Password \n (deve essere uguale al campo precedente)';
		break;
	}

}