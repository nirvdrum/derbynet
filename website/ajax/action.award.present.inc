<?php

// Set "current" award being presented

// $_POST['key'] is one of:
// award-<awardid>
// speed-<place>
// speed-<place>-<den>
// speed-<place>-<den>-<rank>

// If present, $_POST['reveal'] is 0 or 1

require_once('inc/photo-config.inc');
require_once('inc/standings.inc');
require_once('inc/ordinals.inc');

if (have_permission(PRESENT_AWARDS_PERMISSION)) {
  $successful = false;
  if (isset($_POST['key'])) {
    $key = $_POST['key'];
    write_raceinfo('current_award_key', $key);
    $successful = true;
  }
  if (isset($_POST['reveal'])) {
    write_raceinfo('reveal_current_award', $_POST['reveal'] ? 1 : 0);
    $successful = true;
  }
  if ($successful) {
    json_success();
  }
} else {
  json_not_authorized();
}
?>
