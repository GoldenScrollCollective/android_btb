var apiKey = '';
let values = [];

var toolbarOptions = [
  ['bold', 'italic', 'underline'],
  [{ list: 'ordered' }, { list: 'bullet' }],
  ['link']
];

const MentionBlot = Quill.import('blots/mention');

class CustomMentionBlot extends MentionBlot {
  static render(data) {
    const element = document.createElement('span');
    element.innerText = data.label;
    return element;
  }
}
CustomMentionBlot.blotName = 'custom-mention';

Quill.register(CustomMentionBlot);

var quill = new Quill('#editor', {
  placeholder: 'Type your message...',
  modules: {
    toolbar: {
      container: '#toolbar'
    },
    mention: {
      allowedChars: /^[A-Za-z\sÅÄÖåäö]*$/,
      mentionDenotationChars: ['@'],
      source: async function (searchTerm, renderList, mentionChar) {
        if (mentionChar === '@') {
          if (values.length == 0) {
            console.log('apiKey', apiKey);
            const url =
              'https://api.buildthenbless.app/rest/user?' +
              new URLSearchParams({
                q: '',
                order_by: 'name',
                page: '1'
              });
            console.log('url', url);
            try {
              const response = await axios.get(url, {
                headers: { 'X-API-KEY': apiKey }
              });
              for (const key of Object.keys(response)) {
                console.log(key, response[key]);
              }
              if (!response.status) {
                Android.showToast(response.message);
                return;
              }
              values = [];
              response.data.forEach((item) => {
                values.push({
                  id: item.uniq_id,
                  label: item.name,
                  type: 'mention'
                });
              });
            } catch (error) {
              console.log(error);
            }
          }

          if (searchTerm.length === 0) {
            renderList(values, searchTerm);
          } else {
            const matches = [];
            for (i = 0; i < values.length; i++)
              if (
                ~values[i].label.toLowerCase().indexOf(searchTerm.toLowerCase())
              )
                matches.push(values[i]);
            renderList(matches, searchTerm);
          }
        }
      },
      renderLoading: () => {
        return 'Loading...';
      },
      renderItem: (data) => {
        return data.label;
      },
      onSelect: function (item, insertItem) {
        insertItem(item);
      },
      dataAttributes: ['id', 'label', 'type'],
      showDenotationChar: true,
      blotName: 'custom-mention'
    }
  },
  theme: 'snow'
});

const mentionButton = document.querySelector('#insert-mention');
mentionButton.addEventListener('click', function () {
  const range = quill.getSelection();
  quill.insertText(range.index, '@');
  //const module = quill.getModule('mention');
  //module.openMenu('@');
});

function setQuillContent(htmlContent) {
  quill.clipboard.dangerouslyPasteHTML(htmlContent);
}

function setQuillUsers(token) {
  apiKey = token;
}

function sendContentToiOS() {
  var content = quill.root.innerHTML;
  window.webkit.messageHandlers.content.postMessage(content);
}

function insertLink() {
  var url = prompt(
    "Insert a hyperlink (starting with 'https://'):",
    'https://'
  );
  if (url !== null) {
    if (url.startsWith('https://')) {
      quill.focus();
      var selection = quill.getSelection();
      quill.insertText(selection.index, 'Visit Link', { link: url });
    } else {
      alert("Please enter a valid URL starting with 'https://'.");
    }
  }
}
