/*
 * #%L
 * Talend :: ESB :: Job :: Web Console
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
var actions = Array('dc_run', 'dc_undeploy');
$(document).ready(function(){
	$('#bc_grid>tbody>tr').click(function(){
		$isSelected = $(this).is('.selected');
		$('#bc_grid>tbody>tr').removeClass('selected');
		for(x in actions){
			$('#'+actions[x]).addClass('disabled');
			if($isSelected){
				var src1 = $('#'+actions[x]).find('img').attr('src');
				$('#'+actions[x]).find('img').attr('src',src1.replace('.gif','_d.gif'));
			}
		}
		if(!$isSelected){
			for(x in actions){
				$('#'+actions[x]).removeClass('disabled');
				var src = $('#'+actions[x]).find('img').attr('src');
				$('#'+actions[x]).find('img').attr('src',src.replace('_d.gif','.gif'));
			}					
			$(this).addClass('selected');
		}				
	});
	
	$('.bc_btn').hover(
		function(){
			$isDisabled = $(this).is('.disabled');
			if(!$isDisabled){
				$(this).addClass('hover');
			}else{
				$(this).removeClass('hover');
			}
		},
		function(){
			$(this).removeClass('hover');
		}
	);
	
	$('#bc_box_cancel,#bc_box_ok').click(function(){$('#bc_box').fadeOut();});
	$('#dc_deploy').click(function(){$('#bc_box').fadeIn();});
	$('#dc_start').click(function(){
        $('#bc_grid>tbody>tr').each(function() {
            $isSelected = $(this).is('.selected');
            if($isSelected) {
                var url = 'start.do?name=' + $(this).find('.td0').text();
                $(location).attr('href', url);
            }
        });
	});
	$('#dc_stop').click(function(){
	    $('#bc_grid>tbody>tr').each(function() {
	        $isSelected = $(this).is('.selected');
	        if($isSelected) {
                var url = 'stop.do?name=' + $(this).find('.td0').text();
                $(location).attr('href', url);
	        }
	    });
	});
	$('#dc_undeploy').click(function(){
        $('#bc_grid>tbody>tr').each(function() {
            $isSelected = $(this).is('.selected');
            if($isSelected) {
                var url = 'undeploy.do?name=' + $(this).find('.td0').text();
                $(location).attr('href', url);
            }
        });
	});
	$('#dc_run').click(function(){
        $('#bc_grid>tbody>tr').each(function() {
            $isSelected = $(this).is('.selected');
            if($isSelected) {
                var args = window.prompt('Run arguments');
                var url = 'run.do?name=' + $(this).find('.td0').text() + '&args=' + args;
                $(location).attr('href', url);
            }
        });
	});
});