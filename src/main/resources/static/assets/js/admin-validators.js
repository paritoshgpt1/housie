$(function () {
    function hasValidatorFor(code) {
        try {
            return (typeof Validate !== 'undefined') && Validate && (typeof Validate.prototype[code] === 'function');
        } catch (e) {
            return false;
        }
    }

    function render(dividends) {
        const $tbody = $('#validatorsTable tbody');
        $tbody.empty();
        let missing = [];
        dividends.forEach(function (d) {
            const ok = hasValidatorFor(d.code);
            const tr = $('<tr>');
            $('<td>').text(d.name || '').appendTo(tr);
            $('<td>').text(d.code || '').appendTo(tr);
            const $status = $('<td>');
            if (ok) {
                $status.text('Implemented').addClass('status-ok');
            } else {
                $status.text('Missing').addClass('status-missing');
                missing.push(d);
            }
            tr.append($status);
            $tbody.append(tr);
        });

        const $summary = $('#summary');
        $summary.show().text(`Total: ${dividends.length} | Implemented: ${dividends.length - missing.length} | Missing: ${missing.length}`);

        const $missingList = $('#missingList');
        if (missing.length) {
            const list = missing.map(m => m.code).join(', ');
            $missingList.show().text('Missing codes: ' + list);
        } else {
            $missingList.hide().empty();
        }
    }

    function load() {
        const $tbody = $('#validatorsTable tbody');
        $tbody.html('<tr><td colspan="3" class="text-center p-4">Loading…</td></tr>');
        $.get('/api/dividends')
            .done(function (items) { render(items || []); })
            .fail(function () {
                $tbody.html('<tr><td colspan="3" class="text-center text-danger p-4">Failed to load dividends</td></tr>');
            });
    }

    $('#refreshBtn').on('click', function () { load(); });
    load();
});

