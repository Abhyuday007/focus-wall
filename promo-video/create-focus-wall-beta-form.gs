/**
 * Creates the Focus Wall beta application form and a linked response sheet.
 * Run createFocusWallBetaForm() once from https://script.google.com/create
 */
function createFocusWallBetaForm() {
  const form = FormApp.create('Focus Wall — Early Access Beta');

  form
    .setDescription(
      'Focus Wall is a local-first Android app that limits infinite Instagram Reels and YouTube Shorts while keeping useful features such as Stories, DMs, notifications, posts, search, and regular YouTube videos available.\n\n' +
      'This is a small student beta for up to 50 authorized Android devices. Applying does not guarantee a slot. Selected testers will receive a private device-authorization link and installation instructions.\n\n' +
      'Privacy: This form collects only the information needed to select testers, contact them, and understand device compatibility. Do not submit an IMEI, serial number, password, social-media username, private message, or other sensitive information.'
    )
    .setCollectEmail(true)
    .setLimitOneResponsePerUser(true)
    .setProgressBar(true)
    .setConfirmationMessage(
      'Thank you for applying to the Focus Wall beta. If selected, you will receive a private device-authorization link and instructions by email. Never share passwords, device serial numbers, or social-media credentials with anyone claiming to represent Focus Wall.'
    );

  form.addSectionHeaderItem()
    .setTitle('About you')
    .setHelpText('Tell us enough to contact you and understand why Focus Wall may help.');

  form.addTextItem()
    .setTitle('Your name')
    .setRequired(true);

  form.addTextItem()
    .setTitle('GitHub username (optional)')
    .setHelpText('Useful if you would like to report technical issues or follow development.');

  form.addTextItem()
    .setTitle('Country')
    .setHelpText('Play Protect behavior can vary by region.')
    .setRequired(true);

  form.addParagraphTextItem()
    .setTitle('What problem would you like Focus Wall to solve for you?')
    .setHelpText('For example: losing time to Reels, opening Shorts automatically, or wanting to stay available in DMs without doomscrolling.')
    .setRequired(true);

  form.addMultipleChoiceItem()
    .setTitle('How often does short-form scrolling take more time than you intended?')
    .setChoiceValues(['Every day', 'A few times a week', 'A few times a month', 'Rarely'])
    .setRequired(true);

  form.addSectionHeaderItem()
    .setTitle('Your Android device')
    .setHelpText('Focus Wall currently supports Android 10 and newer.');

  form.addTextItem()
    .setTitle('Phone manufacturer and model')
    .setHelpText('Example: Samsung Galaxy S23, Pixel 8, OnePlus 12R. Do not enter an IMEI or serial number.')
    .setRequired(true);

  form.addMultipleChoiceItem()
    .setTitle('Android version')
    .setChoiceValues(['Android 16', 'Android 15', 'Android 14', 'Android 13', 'Android 12', 'Android 11', 'Android 10', 'Not sure'])
    .setRequired(true);

  form.addCheckboxItem()
    .setTitle('Which apps would you test?')
    .setChoiceValues(['Instagram Reels', 'YouTube Shorts'])
    .setRequired(true);

  form.addMultipleChoiceItem()
    .setTitle('Are you comfortable testing a pre-release Android app and reporting problems?')
    .setChoiceValues(['Yes', 'No'])
    .setRequired(true);

  form.addSectionHeaderItem()
    .setTitle('Beta agreement')
    .setHelpText('Focus Wall uses Android Accessibility for its core on-device detection. The app shows a complete disclosure before asking you to enable it.');

  const agreementValidation = FormApp.createCheckboxValidation()
    .requireSelectExactly(3)
    .setHelpText('Select all three statements to apply for the beta.')
    .build();

  form.addCheckboxItem()
    .setTitle('Please confirm all three statements')
    .setChoiceValues([
      'I understand this is pre-release software and may contain bugs.',
      'I will read the in-app Accessibility disclosure before deciding whether to enable protection.',
      'I agree to be contacted by email about beta access and feedback.'
    ])
    .setValidation(agreementValidation)
    .setRequired(true);

  form.addParagraphTextItem()
    .setTitle('Anything else you want the developer to know? (optional)');

  const responses = SpreadsheetApp.create('Focus Wall Beta — Responses');
  form.setDestination(FormApp.DestinationType.SPREADSHEET, responses.getId());

  // Google Forms created after June 30, 2026 may start unpublished.
  // Publishing is required before the responder URL becomes accessible.
  if (form.supportsAdvancedResponderPermissions()) {
    form.setPublished(true);
  } else {
    form.setAcceptingResponses(true);
  }

  console.log('PUBLIC FORM: ' + form.getPublishedUrl());
  console.log('EDIT FORM: ' + form.getEditUrl());
  console.log('RESPONSES: ' + responses.getUrl());
}
