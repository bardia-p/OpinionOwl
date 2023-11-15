let counter = 0;
let numOfQuestions = 0;

const formTitle = $(".form-title");
const survey = $("#survey");
const addTextQuestion = $("#add-text");
const addRadioChoice = $("#add-radio-choice");
const addNumericRange = $("#add-numeric-range");
const submitButton = $("#create-survey");
submitButton.attr("disabled", true);
const generateUniqueID = () => {
    counter++;
    return `unique-${counter}-${Math.floor(Math.random() * 1000)}`;
};

/**
 *
 * @param {string} radioQuestionContainer
 * @param {string} uniqueName
 */
const addMoreRadioOptions = (radioQuestionContainer, uniqueName) => {
    const uniqueId = generateUniqueID();
    $(radioQuestionContainer).append(`
                <br />
                <input id=${uniqueId} type="radio" name=${uniqueName}>
                <label for=${uniqueId} contenteditable="true">sample</label>
        `);
};

/**
 *
 * @param {string} tableRowId
 */
const removeTableRow = (tableRowId) => {
    $(tableRowId).remove();
    decrementNumOfQuestion();
};

const incrementNumOfQuestions = () => {
    numOfQuestions++;
    submitButton.removeAttr('disabled');
};

const decrementNumOfQuestion = () => {
    numOfQuestions--;
    if (numOfQuestions === 0){
        submitButton.attr("disabled", true);
    }
};

addTextQuestion.click((e) => {
    e.preventDefault();
    const rowId = generateUniqueID();
    const question = `
          <tr id=${rowId} class="text-questions">
              <td>
                 <button type="button" onclick="removeTableRow('#${rowId}')">-</button>
              </td>
              <td>
                  <label contenteditable="true">Question title</label>
                  <br />
                  <input type="text" />
              </td>
          </tr>
        `;
    survey.append(question);
    incrementNumOfQuestions();
});

addRadioChoice.click((e) => {
    e.preventDefault();
    const rowId = generateUniqueID();
    const radioQuestionContainer = generateUniqueID();
    const uniqueName = generateUniqueID();
    const question = `
          <tr id='${rowId}' class="radio-questions">
            <td>
               <button type="button" onclick="removeTableRow('#${rowId}')">-</button>
            </td>
            <td>
              <div id=${radioQuestionContainer}>
                <label contenteditable="true" class="title">Question title</label>
                <br />
                <input type="radio" name=${uniqueName}>
                <label contenteditable="true">sample</label>
              </div>
              <button type="button" onclick="addMoreRadioOptions('#${radioQuestionContainer}','${uniqueName}')">+</button>
            </td>
          </tr>
        `;
    survey.append(question);
    incrementNumOfQuestions();
});

addNumericRange.click((e) => {
    e.preventDefault();
    const rowId = generateUniqueID();
    const question = `
          <tr id='${rowId}' class="numeric-questions">
               <td>
                 <button type="button" onclick="removeTableRow('#${rowId}')">-</button>
              </td>
              <td>
                <label contenteditable="true" class="title">Question title</label>
                <br />
                <span contenteditable="false">0</span>
                <input type="range" min="0" max="11" />
                <span contenteditable="false">11</span>
              </td>
          </tr>
        `;
    survey.append(question);
    incrementNumOfQuestions();
});

submitButton.click((e) => {
    e.preventDefault();
    const dataDictionary = {};
    dataDictionary["radioQuestions"] = {};
    dataDictionary["numericRanges"] = {};
    dataDictionary["title"] = formTitle.text();
    let duplicateFound = false;
    // Iterate over table rows with the class 'text-questions'
    const textQuestions = []
    $('.text-questions label').each(function () {
        if (!textQuestions.includes($(this).text())) {
            textQuestions.push($(this).text());
        } else {
            // question is a duplicate
            duplicateFound = true;
        }
    }).get();

    if (duplicateFound) {
        alert("duplicate questions for Text questions. Please make them unique before submitting");
        return;
    }

    dataDictionary["textQuestions"] = textQuestions;

    $('.radio-questions').each(function() {
        const title = $(this).find('.title').text();
        const radioQuestions = [];

        if (dataDictionary["radioQuestions"][title]) {
            // duplicate question
            duplicateFound = true;
            return;
        }

        const radioQuestionContainer = $(this).find('div');
        $(radioQuestionContainer).find('label:not(.title)').each(function () {
            radioQuestions.push($(this).text());
        });
        dataDictionary["radioQuestions"][title] = radioQuestions;
    });

    if (duplicateFound) {
        alert("duplicate questions for Radio questions. Please make them unique before submitting");
        return;
    }

    $('.numeric-questions').each(function() {
        const title = $(this).find(".title").text();
        const ranges = [];

        if (dataDictionary["numericRanges"][title]) {
            // duplicate question
            duplicateFound = true;
            return;
        }

        $(this).find("span").each(function() {
            ranges.push(parseInt($(this).text()));
        });
        dataDictionary["numericRanges"][title] = ranges;
    });

    if (duplicateFound) {
        alert("duplicate questions for numeric range questions. Please make them unique before submitting");
        return;
    }

    console.log(dataDictionary);

    // send post using ajax
    const dataJson = JSON.stringify(dataDictionary);
    $.ajax({
        type: $("#survey-container").attr("method"),
        url: '/api/v1/createSurvey',
        data: dataJson,
        contentType: 'application/json',
        success: function(res) {
            // success handling
            console.log('Survey created successfully');
            if (res === 200) window.location.href = "/";
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error creating survey:', error);
        }
    });
});