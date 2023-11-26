let chartColors = ["red", "green","blue","orange","brown"];

/**
 * Creates a pie chart for the given radio choice question.
 * @param {int} qid
 * @param {string} prompt
 * @param {Object} responses
 */
const displayRadioChoiceResponse = (qid, prompt, responses) => {
    const responseList= $("#radio-choice-container");
    const response = `
            <li>
                <span class="question-title">${prompt}</span>
                <canvas id="chart${qid}" style="width:100%;max-width:700px"></canvas>
            </li>
        `;
    responseList.append(response);
    let xValues = Object.keys(responses);
    let yValues = [];
    let colors = [];
    for (let i in xValues){
        yValues.push(responses[xValues[i]]);
        colors.push(chartColors[i % chartColors.length])
    }
    new Chart("chart" + qid, {
        type: 'pie',
        data: {
            labels: xValues,
            datasets: [{
                label: "Choices",
                data: yValues,
                backgroundColor: colors,
            }]
        },
        options: {
            title:{
                display: false,
                text: prompt
            },
            scales: {
                yAxes: [{
                    ticks: {
                        stepSize: 1.0
                    }
                }]
            }
        }
    });
};

/**
 * Creates a bar chart for the give range question.
 * @param {int} qid
 * @param {string} prompt
 * @param {Object} responses
 */

const displayRangeResponse = (qid, prompt, responses) => {
    const responseList= $("#range-container");
    const response = `
            <li>
                <span class="question-title">${prompt}</span>
                <canvas id="chart${qid}" style="width:100%;max-width:700px"></canvas>
            </li>
        `;
    responseList.append(response);
    let xValues = Object.keys(responses);
    let yValues = [];
    for (let i in xValues){
        yValues.push(responses[xValues[i]]);
    }
    new Chart("chart" + qid, {
        type: 'bar',
        data: {
            labels: xValues,
            datasets: [{
                label: 'Summary of Answers',
                data: yValues,
                backgroundColor: "blue",
            }]
        },
        options: {
            title:{
                display: false,
                text: prompt
            },
            scales: {
                yAxes: [{
                    ticks: {
                        stepSize: 1.0
                    }
                }]
            }
        }
    });
};

/**
 * Generates the appropriate graph for the questions.
 * @param {Object} res
 */
const generateGraphs = (res) => {
    let jsonRes = JSON.parse(res);
    console.log(jsonRes);
    let questions = jsonRes["questions"];
    let responses = jsonRes["responses"];
    for(let q of Object.keys(questions)) {
        // Check to see if there is a response for the question.
        let question = questions[q];
        if (Object.keys(responses[q]).length !== 0){
            if (question["type"] === "Radio Choice") {
                displayRadioChoiceResponse(q, question["prompt"], responses[q]);
            } else if (question["type"] === "Range") {
                displayRangeResponse(q, question["prompt"], responses[q]);
            }
        }
    }
};

$(document).ready(function () {
    let surveyId = $("#surveyId").text();
    $.ajax({
        type: 'GET',
        url: '/api/v1/getSurveyResults/' + surveyId,
        success: function(res) {
            // success handling
            console.log('Survey created successfully');
            generateGraphs(res);
        },
        error: function(xhr, status, error) {
            // error handling
            console.error('Error creating survey:', error);
        }
    });
});