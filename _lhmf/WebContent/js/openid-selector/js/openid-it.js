var providers_large = {
	facebook : {
     name: 'Facebook',
     url: "javascript:facebook_click();"
	},
	google : {
		name : 'Google',
		url : 'https://www.google.com/accounts/o8/id'
	},
	yahoo : {
		name : 'Yahoo',
		url : 'http://me.yahoo.com/'
	},
	/*aol : {
		name : 'AOL',
		label : 'Enter your AOL screenname.',
		url : 'http://openid.aol.com/{username}'
	},*/
	myopenid : {
		name : 'MyOpenID',
		label : 'Inserisci il tuo username MyOpenID.',
		url : 'http://{username}.myopenid.com/'
	},
	openid : {
		name : 'OpenID',
		label : 'Inserisci il tuo OpenID.',
		url : null
	}
};

var providers_small = {
	/*
	livejournal : {
		name : 'LiveJournal',
		label : 'Enter your Livejournal username.',
		url : 'http://{username}.livejournal.com/'
	}, */
	/* flickr: {
		name: 'Flickr',        
		label: 'Enter your Flickr username.',
		url: 'http://flickr.com/{username}/'
	}, */
	/* technorati: {
		name: 'Technorati',
		label: 'Enter your Technorati username.',
		url: 'http://technorati.com/people/technorati/{username}/'
	}, */
	wordpress : {
		name : 'Wordpress',
		label : 'Inserisci il tuo username Wordpress.com.',
		url : 'http://{username}.wordpress.com/'
	},
	blogger : {
		name : 'Blogger',
		label : 'Il tuo account Blogger',
		url : 'http://{username}.blogspot.com/'
	},
	/*
	verisign : {
		name : 'Verisign',
		label : 'Your Verisign username',
		url : 'http://{username}.pip.verisignlabs.com/'
	}, */
	/* vidoop: {
		name: 'Vidoop',
		label: 'Your Vidoop username',
		url: 'http://{username}.myvidoop.com/'
	}, */
	launchpad: {
		name: 'Launchpad',
		label: 'Inserisci il tuo username Launchpad',
		url: 'https://launchpad.net/~{username}'
	}/*, 
	claimid : {
		name : 'ClaimID',
		label : 'Your ClaimID username',
		url : 'http://claimid.com/{username}'
	}, */
	/*
	clickpass : {
		name : 'ClickPass',
		label : 'Enter your ClickPass username',
		url : 'http://clickpass.com/public/{username}'
	},*/
	/*google_profile : {
		name : 'Google Profile',
		label : 'Enter your Google Profile username',
		url : 'http://www.google.com/profiles/{username}'
	}*/
};

openid.locale = 'it';
openid.sprite = 'it'; // reused in german& japan localization
openid.demo_text = 'In client demo mode. Normally would have submitted OpenID:';
openid.signin_text = 'Accedi';
openid.image_title = 'accedi con {provider}';
