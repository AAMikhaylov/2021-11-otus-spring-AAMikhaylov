function saveData(entityName, formId, saveUrl, redirectUrl) {
    let formData = new FormData(document.getElementById(formId));
    let jsonObject = Object.fromEntries(formData.entries());
    if ('genres' in jsonObject)
        jsonObject.genres = formData.getAll("genres");
    if (jsonObject.id != 0) {
        sendMethod = 'PUT';
        saveUrl = saveUrl + '/' + id;
    } else {
        sendMethod = 'POST';
        jsonObject.id = null;
    }
    let jsonBody = JSON.stringify(jsonObject);
    fetch(saveUrl, {
        method: sendMethod,
        cache: "no-cache",
        headers: {"Content-Type": "application/json"},
        body: jsonBody
    })
        .then(function (resp) {
            if (!resp.ok) {
                resp.json()
                    .then(function (jsonError) {
                        if (resp.status === 400) {
                            showBindingErrors(entityName, jsonError);
                        } else {
                            showGlobalError(jsonError);
                        }
                    });
                return;
            }
            resp.text()
                .then(savedId => location.href = redirectUrl + '?id=' + savedId);
        });
}

async function loadData(entityName, url) {
    let resp = await fetch(url);
    let jsonData = await resp.json();
    if (!resp.ok)
        showGlobalError(jsonData);
    else
        initDataFields(entityName, jsonData);
    return jsonData;
}

function deleteEntity(deleteUrl, redirectUrl, confirmMessage) {
    if (!confirm(confirmMessage))
        return;
    fetch(deleteUrl,
        {
            method: "DELETE",
            cache: "no-cache",
            headers: {"Content-Type": "application/json"}
        })
        .then(function (response) {
            if (!response.ok) {
                response.json()
                    .then(jsonError => showGlobalError(jsonError));
                return;
            }
            location.href = redirectUrl;
        });
}

function initDataFields(entityName, jsonObject) {
    let keys = Object.keys(jsonObject);
    keys.forEach(function (key) {
        let field = document.getElementById(entityName + '.' + key);
        if (field != null) {
            if (field instanceof HTMLInputElement)
                field.value = jsonObject[key];
            if ((field instanceof HTMLTableCellElement) || (field instanceof HTMLTextAreaElement))
                field.innerHTML = jsonObject[key];
        }
    });
    let nameIdFields = document.getElementsByName("id");
    if (nameIdFields.length !== 0) {
        nameIdFields[0].value = jsonObject.id;
    }


}

function showBindingErrors(entityName, errData) {
    if (errData.errors === undefined || errData.errors.length < 1) {
        showGlobalError(errData);
        return;
    }
    let ulElems = document.body.getElementsByTagName("ul");
    for (i = 0; i < ulElems.length; i++) {
        if (ulElems.item(i).id.includes("Error")) {
            ulElems.item(i).innerHTML = "";
        }
    }
    errData.errors.forEach(function (error) {
            let ulElement = document.getElementById(entityName + '.' + error.field + 'Errors');
            if (ulElement != null) {
                let liElement = document.createElement("li");
                liElement.appendChild(document.createTextNode(error.defaultMessage));
                ulElement.appendChild(liElement);
            }
        }
    );
}

function showGlobalError(errData) {
    let errMessage;
    if (typeof (errData) == 'string' || typeof (errData) == 'number') {
        errMessage = errData;
    } else if (errData != null && typeof (errData) == 'object') {
        let keys = Object.keys(errData);
        if (keys.includes('message')) {
            errMessage = errData.message;
            if (errMessage === "") {
                errMessage = errData.status + ' - ' + errData.error + '(' + errData.exception + ')';
            }
        }
    }
    if (typeof (errMessage) == 'undefined') {
        errMessage = 'Unknown error';
    }
    let errorField = errorDiv;
    if (errorField == null || errorDiv === undefined)
        console.log(errMessage);
    else
        errorField.innerHTML = errMessage;
}



