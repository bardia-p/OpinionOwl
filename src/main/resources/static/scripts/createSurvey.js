let counter = 0;
let numOfQuestions = 0;

const formTitle = $(".form-title");
const survey = $("#survey");
const addTextQuestion = $("#add-text");
const addRadioChoice = $("#add-radio-choice");
const addNumericRange = $("#add-numeric-range");
const formButton = $(".create-edit-submit-btn");
const submitButton = $("#create-survey");
formButton.attr("disabled", true);
const generateUniqueID = () => {
    counter++;
    return `unique-${counter}-${Math.floor(Math.random() * 1000)}`;
};

/**
 *
 * @param {string} radioQuestionContainer
 * @param {string} prompt
 */
const addMoreRadioOptions = (radioQuestionContainer, prompt= "Sample") => {
    const uniqueId = generateUniqueID();
    const divId = generateUniqueID();
    $(radioQuestionContainer).append(`
                <div id=${divId} class="radio-container flex">
                    <input id=${uniqueId} type="radio">
                    <label for=${uniqueId} contenteditable="true">${prompt}</label>
                    <button class="btn" onclick="removingRadioChoice('${radioQuestionContainer}', '#${divId}')">X</button>
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
    formButton.removeAttr('disabled');
};

const decrementNumOfQuestion = () => {
    numOfQuestions--;
    if (numOfQuestions === 0){
        formButton.attr("disabled", true);
    }
};

const addTextQuestionRow = (prompt = "Question Title") => {
    const rowId = generateUniqueID();
    const question = `
          <tr id=${rowId} class="text-questions">
              <td>
                 <button class="btn" type="button" onclick="removeTableRow('#${rowId}')">X</button>
              </td>
              <td>
                  <label contenteditable="true">${prompt}</label>
                  <input type="text" />
              </td>
          </tr>
        `;
    survey.append(question);
    incrementNumOfQuestions();
}

addTextQuestion.click((e) => {
    e.preventDefault();
    addTextQuestionRow();
});

const addRadioChoicesRow = (prompt= "Question Title", choices= ["Sample"]) => {
    const rowId = generateUniqueID();
    const radioQuestionContainer = generateUniqueID();

    const question = `
          <tr id='${rowId}' class="radio-questions">
            <td>
               <button class="btn" type="button" onclick="removeTableRow('#${rowId}')">X</button>
            </td>
            <td>
              <div id=${radioQuestionContainer}>
                <label contenteditable="true" class="title">${prompt}</label>
              </div>
              <button class="btn add-more-radio-choices" type="button" onclick="addMoreRadioOptions('#${radioQuestionContainer}')">+</button>
            </td>
          </tr>
        `;
    survey.append(question);
    for (const choice of choices) {
        addMoreRadioOptions(`#${radioQuestionContainer}`, choice);
    }
    incrementNumOfQuestions();
}

addRadioChoice.click((e) => {
    e.preventDefault();
    addRadioChoicesRow();
});

const addRangeQuestionRow = (prompt = "Question Title", ranges = [0, 10]) => {
    const rowId = generateUniqueID();
    const question = `
          <tr id='${rowId}' class="numeric-questions">
               <td>
                 <button class="btn" type="button" onclick="removeTableRow('#${rowId}')">X</button>
              </td>
              <td>
                <label contenteditable="true" class="title">${prompt}</label>
                <div class="flex range-option">
                    <span contenteditable="true">${ranges[0]}</span>
                    <input type="range" min="0" max="11" />
                    <span contenteditable="true">${ranges[1]}</span>
                </div>
              </td>
          </tr>
        `;
    survey.append(question);
    incrementNumOfQuestions();
}

addNumericRange.click((e) => {
    e.preventDefault();
    addRangeQuestionRow();
});

const parseSurveyFormData = () => {
    const dataDictionary = {};
    dataDictionary["radioQuestions"] = {};
    dataDictionary["numericRanges"] = {};
    dataDictionary["title"] = formTitle.text();

    if(!formTitle.text()) {
        setToast("warning", "Empty Field", "Please put a title");
        return
    }

    let duplicateFound = false;
    let emptyLabelFound = false;
    let invalidRange = false;
    // Iterate over table rows with the class 'text-questions'
    const textQuestions = []
    $('.text-questions label').each(function () {
        if (!$(this).text()) {
            emptyLabelFound = true;
        }
        if (!textQuestions.includes($(this).text())) {
            textQuestions.push($(this).text());
        } else {
            // question is a duplicate
            duplicateFound = true;
        }
    }).get();

    if (emptyLabelFound) {
        setToast("warning", "Empty Field", "Please put a question for text questions");
        return
    }

    if (duplicateFound) {
        setToast("warning", "Duplicate Questions", "Text input questions needs to be unique");
        return;
    }

    dataDictionary["textQuestions"] = textQuestions;

    $('.radio-questions').each(function() {
        const title = $(this).find('.title').text();
        const radioQuestions = [];

        if (!title){
            emptyLabelFound = true;
        }
        if (dataDictionary["radioQuestions"][title]) {
            // duplicate question
            duplicateFound = true;
            return;
        }

        const radioQuestionContainer = $(this).find('div');
        $(radioQuestionContainer).find('label:not(.title)').each(function () {
            if(!$(this).text()){
                emptyLabelFound = true
            }
            else {
                radioQuestions.push($(this).text());
            }
        });
        dataDictionary["radioQuestions"][title] = radioQuestions;
    });

    if (duplicateFound) {
        setToast("warning", "Duplicate Questions", "Radio choice questions needs to be unique");
        return;
    }

    if (emptyLabelFound) {
        setToast("warning", "Empty Fields", "Please put a question for the radio choices");
        return
    }

    $('.numeric-questions').each(function() {
        const title = $(this).find(".title").text();
        const ranges = [];

        if (!title){
            emptyLabelFound = true;
        }

        if (dataDictionary["numericRanges"][title]) {
            // duplicate question
            duplicateFound = true;
            return;
        }

        $(this).find("span").each(function() {
            if ($(this).text()){
                if (!isNaN(parseInt($(this).text()))) {
                    ranges.push(parseInt($(this).text()));
                }
                else {
                    invalidRange = true;
                    return;
                }
            }
            else {
                emptyLabelFound = true;
                return;
            }
        });
        dataDictionary["numericRanges"][title] = ranges;
    });

    if (emptyLabelFound) {
        setToast("warning", "Empty Fields", "Please put inputs for the numeric ranges and questions");
        return;
    }

    if (invalidRange) {
        setToast("warning", "Range Invalid Input", "Ranges needs to be numeric!");
        return;
    }

    if (duplicateFound) {
        setToast("warning", "Duplicate Questions", "Range Questions needs to be unique");
        return;
    }

    return dataDictionary;
}

submitButton.click((e) => {
    e.preventDefault();
    const dataDictionary = parseSurveyFormData();
    if (!dataDictionary) {
        return
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
                setToast("success", "Survey Created!", "Survey Successfully Created", true);
                window.location.href = "/";
            } else {
                setToast("error", "Something went wrong", "You cannot complete this request at the moment");
            }
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error creating survey:', error);
            setToast("error", "Something went wrong", "Could not create survey");
        }
    });
});