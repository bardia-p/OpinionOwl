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
    const divId = generateUniqueID();
    $(radioQuestionContainer).append(`
                <div id=${divId} class="radio-container flex">
                    <input id=${uniqueId} type="radio">
                    <label for=${uniqueId} contenteditable="true">sample</label>
                    <button class="btn" onclick="removingRadioChoice('${radioQuestionContainer}', '#${divId}')">-</button>
                </div>
        `);
    $(`${radioQuestionContainer} .radio-container`).find('button').prop("disabled", $(`${radioQuestionContainer}`).find('.radio-container').length === 1);
};

/**
 * remove an element by id
 * @param {string} id the id
 */
const removeElement = (id) => {
    $(id).remove();
}

/**
 *
 * @param {string} radioQuestionContainer
 * @param {string} divId
 */
const removingRadioChoice = (radioQuestionContainer, divId) => {
    removeElement(divId);
    $(`${radioQuestionContainer} .radio-container`).find('button').prop("disabled", $(`${radioQuestionContainer}`).find('.radio-container').length === 1);
}

/**
 *
 * @param {string} tableRowId
 */
const removeTableRow = (tableRowId) => {
    removeElement(tableRowId);
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
                 <button class="btn" type="button" onclick="removeTableRow('#${rowId}')">-</button>
              </td>
              <td>
                  <label contenteditable="true">Question title</label>
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
               <button class="btn" type="button" onclick="removeTableRow('#${rowId}')">-</button>
            </td>
            <td>
              <div id=${radioQuestionContainer}>
                <label contenteditable="true" class="title">Question title</label>
              </div>
              <button class="btn add-more-radio-choices" type="button" onclick="addMoreRadioOptions('#${radioQuestionContainer}','${uniqueName}')">+</button>
            </td>
          </tr>
        `;
    survey.append(question);
    addMoreRadioOptions(`#${radioQuestionContainer}`, `#${uniqueName}`)
    incrementNumOfQuestions();
});

addNumericRange.click((e) => {
    e.preventDefault();
    const rowId = generateUniqueID();
    const question = `
          <tr id='${rowId}' class="numeric-questions">
               <td>
                 <button class="btn" type="button" onclick="removeTableRow('#${rowId}')">-</button>
              </td>
              <td>
                <label contenteditable="true" class="title">Question title</label>
                <div class="flex range-option">
                    <span contenteditable="true">0</span>
                    <input type="range" min="0" max="11" />
                    <span contenteditable="true">11</span>
                </div>
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
            if (res === 200) {
                console.log('Survey created successfully');
                window.location.href = "/";
            } else {
                alert("Could not create the survey!");
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error creating survey:', error);
        }
    });
});