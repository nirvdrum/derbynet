<?php

require_once('inc/newracer.inc');
require_once('inc/sql-script.inc');
require_once(sql_file_path('column-widths'));
require_once('inc/partitions.inc');
require_once('inc/schema_version.inc');


function as_boolean($val) {
  if (is_string($val)) {
    $val = trim($val);
  }
  if ($val === true || $val === 1 || $val === '1') return true;
  if ($val === false || $val === 0 || $val === '0' || $val === '') return false;
  $val = strtolower($val)[0];
  return $val != 'n';
}

if (have_permission(SET_UP_PERMISSION)) {

  // $fields tells what parameters we expect, and whether each is required.
  // Note that we don't actually test for unexpected parameters; we just ignore
  // them.
  $fields = array('lastname' => true,  
                  'firstname' => true,
                  'partition' => false,
                  'carnumber' => false,
                  'carname' => false,
                  'exclude' => false);

  // An alternative to separate lastname and firstname parameters is a single
  // combined field, called first-last.  The content can either be firstname
  // then lastname separated by a space, or lastname then firstname separated by
  // comma and optional space.
  if (isset($_POST['first-last'])) {
    $comma = strpos($_POST['first-last'], ',');
    if ($comma === false) {
      // firstname then lastname, space-separated.  Use the last space after
      // trimming as the dividing point.
      $first_last = trim($_POST['first-last']);
      $space = strrpos($first_last, ' ');
      if ($space === false) {
        $_POST['firstname'] = '';
        $_POST['lastname'] = $first_last;
      } else {
        // These will get trimmed later
        $_POST['firstname'] = substr($first_last, 0, $space);
        $_POST['lastname'] = substr($first_last, $space + 1);
      }
    } else {
      // Comma signals lastname then firstname
      // These will get trimmed later
      $_POST['firstname'] = substr($_POST['first-last'], $comma + 1);
      $_POST['lastname'] = substr($_POST['first-last'], 0, $comma);
    }
  }

  $ok = true;

  foreach ($fields as $fieldname => $required) {
    if ($required && !isset($_POST[$fieldname])) {
      json_failure('missingrequired', "Missing required field $fieldname.");
      $ok = false;
      break;
    }
  }
} else {
  json_not_authorized();
  $ok = false;
}

if ($ok) {
  $carnumber = isset($_POST['carnumber']) ? $_POST['carnumber'] : '';
  $carname = isset($_POST['carname']) ? $_POST['carname'] : '';

  $exclude = isset($_POST['exclude']) ? as_boolean($_POST['exclude']) : false;

  $options = array('firstname' => $_POST['firstname'],
                   'lastname' => $_POST['lastname'],
                   'carnumber' => $carnumber,
                   'carname' => $carname,
                   'exclude' => $exclude);
  if (isset($_POST['partition'])) {
    $options['partition'] = trim(@$_POST['partition']);
  }
  insert_new_racer($options);

  json_success();
}
?>