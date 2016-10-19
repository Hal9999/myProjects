$(document).ready(function()
{
	$("#buySubmit").click(function()
	{
		var numberRegexp = /^[1-9][0-9]*$/;
		
		var quantity = $('#buyQuantityField').val();

		$('.error').hide();
		
		if(quantity=="")
            $('#buyQuantityField').after('<span class="error">Inserisci una quantit&agrave</span>');
        else if(!numberRegexp.test(quantity) || quantity<=0)
            $('#buyQuantityField').after('<span class="error">Inserisci un numero intero positivo</span>');
        else return true;
		
		return false;
	});
	
	$("#sellSubmit").click(function()
	{
		var numberRegexp = /^[1-9][0-9]*$/;
		var quantity = $('#sellQuantityField').val();

		$('.error').hide();
		
		if(quantity=="")
            $('#sellQuantityField').after('<span class="error">Inserisci una quantit&agrave</span>');
        else if(!numberRegexp.test(quantity) || quantity<=0)
            $('#sellQuantityField').after('<span class="error">Inserisci un numero intero positivo</span>');
        else return true;
		
		return false;
	});
	
	$("#loginSubmit").click(function()
	{
		var emailRegexp = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$/;
		var passwordRegexp = /^[a-zA-Z0-9]{2,}$/;
		
		var email = $('#userEmailField').val();
		var password = $('#userPasswordField').val();

		$('.error').hide();
		
		if(email=="")
            $('#userEmailField').after('<span class="error">Inserisci una email</span>');
        else if(!emailRegexp.test(email))
            $('#userEmailField').after('<span class="error">Inserisci una email valida</span>');
        else if(password=="")
            $('#userPasswordField').after('<span class="error">Inserisci una password</span>');
        else if(!passwordRegexp.test(password))
            $('#userPasswordField').after('<span class="error">Inserisci una password alfanumerica di almeno 2 caratteri</span>');
        else return true;
		
		return false;
	});
	
	$("#registerSubmit").click(function()
	{
		var emailRegexp = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$/;
		var nameRegexp = /^[a-zA-Z']+$/;
		var surnameRegexp = nameRegexp;
		var passwordRegexp = /^[a-zA-Z0-9]{2,}$/;
		
		var email = $('#userEmailField').val();
		var name = $('#userNameField').val();
		var surname = $('#userSurnameField').val();
		var password = $('#userPasswordField').val();
		var password2 = $('#userPasswordField2').val();

		$('.error').hide();
		
		if(email=="")
            $('#userEmailField').after('<span class="error">Inserisci una email</span>');
        else if(!emailRegexp.test(email))
            $('#userEmailField').after('<span class="error">Inserisci una email valida</span>');
        else if(name=="")
            $('#userNameField').after('<span class="error">Inserisci un nome</span>');
        else if(!nameRegexp.test(name))
            $('#userNameField').after('<span class="error">Inserisci un nome valido</span>');
        else if(surname=="")
            $('#userSurnameField').after('<span class="error">Inserisci un cognome</span>');
        else if(!surnameRegexp.test(surname))
            $('#userSurnameField').after('<span class="error">Inserisci un cognome valido</span>');
        else if(password=="")
            $('#userPasswordField').after('<span class="error">Inserisci una password</span>');
        else if(!passwordRegexp.test(password))
            $('#userPasswordField').after('<span class="error">Inserisci una password alfanumerica di almeno 2 caratteri</span>');
        else if(password2=="")
            $('#userPasswordField2').after('<span class="error">Ripeti la password</span>');
        else if(!passwordRegexp.test(password2))
            $('#userPasswordField2').after('<span class="error">Ripeti la password</span>');
        else if(password!=password2)
        	$('#userPasswordField2').after('<span class="error">Le password non coincidono</span>');
        else return true;
		
		return false;
	});
});