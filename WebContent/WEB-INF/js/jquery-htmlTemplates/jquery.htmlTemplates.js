(function(jQuery) {

    // loads templates into memory
    jQuery.template = function(source) {
        jQuery(source).css('display', 'none');
        // load any existing template data
        var templateData = jQuery(document).data('templates') || {};

        // iterate through each source element and add templates
        jQuery(source).children().each(function() {
            var template = jQuery(this);
            var prefix = template.parent().attr('id');
            if (prefix) prefix += '_';
            var key = (template.attr('id') || '').replace(prefix, '');
            if (key) { // ignores elements with no id            
                templateData[key] = template.removeAttr('id'); // templates with the same key will get overwritten
            }
            else if (console && console.warn) {
                console.warn('Encountered a template with no identifier.', template);
            }
        });

        jQuery(document).data('templates', templateData);
        return jQuery;
    };

    // injects data into a template and adds it into the dom
    jQuery.fn.template = function(templateName, data, addMethod) {
        if (arguments.length == 0) {
            jQuery.template(this);
            return this;
        }
        else {
            var templateData = jQuery(document).data('templates');
            if (!templateData) throw ('No templates have been loaded!');

            var template = templateData[templateName];
            if (!template) throw ('Missing template requested: \'' + templateName + '\'');

            var result = [];
            if (data) {
                // determine if the data is an array or not
                if (data.constructor.toString().indexOf('Array') >= 0) {
                    for(var i = 0; i < data.length; i++) {
                        result.push(jQuery.template.populateTemplate(template, data[i]));
                    }
                }
                else result.push(jQuery.template.populateTemplate(template, data));
            }
            
            jQuery(this)[addMethod || 'append'](result.join(''));
        }
        return this;
    };
    
    // injects data from a single item into a template
    jQuery.template.populateTemplate = function(template, item) {
        var target = template.clone(true);
        for (var prop in item) {
            target.find('.' + prop).text(item[prop]);
        }
        return target.wrap('<div></div>').parent().html();
    };
})(jQuery);