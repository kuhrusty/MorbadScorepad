(function(global){
    "use strict";

    var document = global.document;
    
    global.morbad = {
        setSkills: function(json){
            this.skills = json;
        },

        miniMarkup: function(text){
            var tmp, padKludge = ' ';
            text = (padKludge + text + padKludge);
            var doSimpleMarkup = function(tag,openerClass,txt){
                var a = [];
                var tmp = txt.split(tag);
                if(1===tmp.length) return txt;
                var tmp2 = [];
                if(0===tmp.length%2){
                    var msg = "Mismatched '"+tag+"' count in input.";
                    console.error(msg,txt);
                    throw new Error(msg);
                }
                while(tmp.length > 0){
                    // bug: does not handle mismatched '***' pairs.
                    tmp2.push(tmp.shift());
                    if(tmp.length){
                        tmp2.push('<span class="'+openerClass+'">');
                        tmp2.push(tmp.shift());
                        tmp2.push('</span>');
                    }
                }
                return tmp2.join('');
            };
            text = doSimpleMarkup('***','italic strong',text);
            text = doSimpleMarkup('**','strong',text);
            text = doSimpleMarkup('*','italic',text);
            return text.trim();
        },

        map:{
            keywords:{
                HEAVY: {
                    label: "Heavy",
                    img: "glyphs/black/heavy.svg"
                },
                XP: {
                    label: "XP",
                    img: "glyphs/black/xp.svg"
                },
                XP_ALT: {
                    label: "XP",
                    img: "glyphs/yellow/xp-alt.svg"
                }
            },
            keywordRef: function(e){
                return '<span class="dd-keyword-ref">'+
                    e.label+'<img src="'+
                    e.img+'"/></span>';
            }
        },

        runCardMarkup:function(cardHtml /*complete .dd-card-wrapper HTML or DOM element*/){
            var k, kwd, origElem;
            if('string' !== typeof cardHtml){
                origElem = cardHtml;
                cardHtml = cardHtml.innerHTML;
            }
            for(k in this.map.keywords){
                if(!this.map.keywords.hasOwnProperty(k)) continue;
                kwd = this.map.keywords[k];
                cardHtml = cardHtml.replace(new RegExp('\\{:'+k+':}', 'g'),
                                  this.map.keywordRef(kwd));
            }
            var elem = document.createElement('div');
            elem.innerHTML = cardHtml;
            var mbad = this;
            elem.querySelectorAll('.dd-body-section')
                .forEach(function(e){
                    var ihtml = e.innerHTML.trim();
                    e.innerHTML = mbad.miniMarkup(
                        ihtml
                            .replace(/(\r?\n)?[ \t]*---(\r?\n[ \t]*)*/g, '<hr/>')
                        /*  ^^^^ Treat --- as an HR. */
                            .replace(/(\r?\n[ \t]*)/g,'<br/>')
                        /* ^^^ Treat each set of 1-2 newlines as a single BR.
                           Inject blank lines in the output by using 2 blank
                           lines between text lines. */
                    );
                });
            elem.querySelectorAll('.dd-req-list')
                .forEach(function(e){
                    var theOr = '<span class="no-text-transform">or</span>';
                    var a = e.innerHTML.split(/\bor\b/ig);
                    if(a.length>1){
                        e.innerHTML = a.join(theOr);
                    }
                });
            if(origElem){
                origElem.innerHTML = elem.innerHTML;
                return origElem;
            }else{
                return elem.innerHTML;
            }
        }
    };

    
    var cardWrapper = morbad.runCardMarkup(
        document.querySelector('.dd-card-wrapper')
    );


    var theClone = document.querySelector('#card-clone');
    theClone.innerHTML = cardWrapper.innerHTML;
    theClone.classList.add('dd-card-wrapper');
    var cards = [cardWrapper, theClone];

    var theCard = theClone.querySelector('.dd-the-card');
    theCard.classList.add('mastery');
    theCard.classList.remove('skill');
    theCard = undefined;


    

    // animation control vaguely derived from:
    // https://jonsuh.com/blog/detect-the-end-of-css-animations-and-transitions-with-javascript/
    var animEventName = 'animationend';
    var animationDone = function(enableElem, removeClass){
        return function callee(){
            console.debug(animEventName,removeClass);
            if(removeClass){
                cards.forEach(function(c){
                    if('string' === typeof removeClass){
                        c.classList.remove(removeClass);
                    }else{
                        c.classList.remove.apply(c.classList, removeClass);
                    }
                });
            }
            enableElem.removeAttribute('disabled');
            this.removeEventListener(animEventName, callee);
        };
    };
    
    var btnScale = document.querySelector('button.btn-toggle-scale');
    if(btnScale){
        btnScale.addEventListener('click', function callee(){
            var scales;
            if(!callee.scales){
                callee.scales = [0.75, 0.5, 1.0];
                callee.scales.ndx = 0;
            }
            scales = callee.scales;
            cards.forEach(function(c){
                c.style.transform = 'scale('+scales[scales.ndx]+')';
            });
            scales.ndx = (scales.ndx + 1) % scales.length;
        }, false);
    }

    
    var btnBling = document.querySelector('button.btn-bling');
    if(btnBling){
        btnBling.addEventListener('click', function(){
            var blingClass = 'bling-rotate-scale';
            this.setAttribute('disabled','disabled');
            cards[cards.length-1]
                .addEventListener(animEventName,
                                  animationDone(this, blingClass));
            cards.forEach(function(c){
                c.classList.add(blingClass);
            });
        }, false);
    }

    var btnTap = document.querySelector('button.btn-tap');
    if(btnTap){
        btnTap.addEventListener('click', function callee(){
            if(undefined===callee.tapped) callee.tapped = 0;
            var blingClass = callee.tapped++ ? 'bling-untap' : 'bling-tap';
            this.setAttribute('disabled','disabled');
            cards[cards.length-1]
                .addEventListener(animEventName,
                                  animationDone(this)//, blingClass)//, ['bling-untap', 'bling-tap'])
                                 );
            cards.forEach(function(c){
                if(c.classList.contains('bling-tap')){
                    c.classList.add('bling-untap');
                    c.classList.remove('bling-tap');
                }else{
                    c.classList.add('bling-tap');
                    c.classList.remove('bling-untap');
                }
            });
        }, false);
    }

})(window);
