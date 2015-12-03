/*
 *   Copyright (C) 2015 Karumi.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.karumi.marvelapiclient;

import com.karumi.marvelapiclient.model.CharacterDto;
import com.karumi.marvelapiclient.model.CharactersDto;
import com.karumi.marvelapiclient.model.CharactersQuery;
import com.karumi.marvelapiclient.model.ComicResource;
import com.karumi.marvelapiclient.model.MarvelImage;
import com.karumi.marvelapiclient.model.MarvelResources;
import com.karumi.marvelapiclient.model.MarvelResponse;
import com.karumi.marvelapiclient.model.MarvelUrl;
import com.karumi.marvelapiclient.model.StoryResource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.junit.Test;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static org.junit.Assert.assertEquals;

public class CharacterApiClientTest extends ApiClientTest {

  private static final String ANY_PUBLIC_KEY = "1234";
  private static final String ANY_PRIVATE_KEY = "abcd";
  private static final String ANY_URL = "http://fake.marvel.com/";
  private static final int ANY_OFFSET = 1;
  private static final int ANY_LIMIT = 10;
  private static final int INVALID_LIMIT = 0;
  private static final String ANY_NAME = "Spider-man";
  private static final String ANY_START_NAME = "Spider";
  private static final int ANY_COMIC_1 = 1;
  private static final int ANY_COMIC_2 = 2;
  private static final int ANY_SERIE = 1;
  private static final int ANY_STORY = 1;
  private static final int ANY_EVENT_1 = 1;
  private static final int ANY_EVENT_2 = 2;
  private static final String ANY_MODIFIED_SINCE = "2015-01-09T22:10:45-0800";
  private static final String ORDER_NAME_DESCEDANT_VALUE = "-name";

  @Test public void shouldReturnAValidUrlWhenICallToGetAllWithValidOffsetAndLimit()
      throws Exception {
    CharacterApiClient characterApiClient = givenCharacterApiClient();
    enqueueMockResponse(200);

    characterApiClient.getAll(ANY_OFFSET, ANY_LIMIT);

    assertRequestSentToContains("offset=" + ANY_OFFSET, "limit=" + ANY_LIMIT);
  }

  @Test(expected = IllegalArgumentException.class) public void shouldThrowExceptionWhenLimitIsZero()
      throws Exception {
    CharacterApiClient characterApiClient = givenCharacterApiClient();
    enqueueMockResponse(200);

    characterApiClient.getAll(ANY_OFFSET, INVALID_LIMIT);
  }

  @Test public void shouldReturnValidUrlWhenICallToGetAllWithBuilder() throws Exception {
    CharacterApiClient characterApiClient = givenCharacterApiClient();
    enqueueMockResponse(200);

    CharactersQuery query = CharactersQuery.Builder.create()
        .withName(ANY_NAME)
        .withNameStartWith(ANY_START_NAME)
        .withLimit(ANY_LIMIT)
        .withOffset(ANY_OFFSET)
        .withModifiedSince(getAnyDate())
        .withOrderBy(CharactersQuery.OrderBy.NAME, false)
        .addComic(ANY_COMIC_1)
        .addComic(ANY_COMIC_2)
        .addEvents(getAnyEvents())
        .addSerie(ANY_SERIE)
        .addStory(ANY_STORY)
        .build();
    characterApiClient.getAll(query);

    assertRequestSentToContains("offset=" + ANY_OFFSET, "limit=" + ANY_LIMIT, "name=" + ANY_NAME,
        "nameStartsWith=" + ANY_START_NAME, "modifiedSince=" + ANY_MODIFIED_SINCE,
        "orderBy=" + ORDER_NAME_DESCEDANT_VALUE, "comics=1,2", "events=1,2", "series=1",
        "stories=1");
  }

  @Test(expected = MarvelApiException.class) public void shouldThrowExceptionWhenApiReturnAnError()
      throws Exception {
    CharacterApiClient characterApiClient = givenCharacterApiClient();
    enqueueMockResponse(401,
        "{\"code\":\"InvalidCredentials\",\"message\":\"That hash, timestamp and key "
        + "combination is invalid.\"}");

    try {
      characterApiClient.getAll(ANY_OFFSET, ANY_LIMIT);
    } catch (MarvelApiException e) {
      assertEquals("InvalidCredentials", e.getMarvelCode());
      assertEquals("That hash, timestamp and key combination is invalid.", e.getMessage());
      throw e;
    }
  }

  @Test public void shouldReturnAValidResponseWhenCallGetAll() throws Exception {
    CharacterApiClient characterApiClient = givenCharacterApiClient();
    enqueueMockResponse("getCharacters.json");

    MarvelResponse<CharactersDto> characters = characterApiClient.getAll(0, ANY_LIMIT);

    assertBasicMarvelResponse(characters);
    CharactersDto charactersDto = characters.getResponse();
    assertEquals(10, charactersDto.getCount());
    assertEquals(10, charactersDto.getLimit());
    assertEquals(0, charactersDto.getOffset());
    assertEquals(1485, charactersDto.getTotal());

    CharacterDto firstCharacter = charactersDto.getCharacters().get(0);
    assertEquals("1011334", firstCharacter.getId());
    assertEquals("3-D Man", firstCharacter.getName());
    assertEquals("3-D man is a 3d superhero", firstCharacter.getDescription());
    assertEquals("2014-04-29T14:18:17-0400", firstCharacter.getModified());
    assertEquals("http://gateway.marvel.com/v1/public/characters/1011334",
        firstCharacter.getResourceUri());

    MarvelImage thumbnail = firstCharacter.getThumbnail();
    assertEquals("http://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784", thumbnail.getPath());
    assertEquals("jpg", thumbnail.getExtension());

    List<MarvelUrl> urls = firstCharacter.getUrls();
    assertEquals(3, urls.size());
    MarvelUrl marvelUrl = urls.get(0);
    assertEquals("detail", marvelUrl.getType());
    assertEquals("http://marvel.com/characters/74/3-d_man?utm_campaign=apiRef&utm_source="
                 + "838a08a2f4c39fa3fd218b1b2d43f19a", marvelUrl.getUrl());

    MarvelResources<ComicResource> comics = firstCharacter.getComics();
    assertEquals(11, comics.getAvailable());
    assertEquals(11, comics.getReturned());
    assertEquals("http://gateway.marvel.com/v1/public/characters/1011334/comics",
        comics.getCollectionUri());
    assertEquals(11, comics.getItems().size());
    ComicResource firstComic = comics.getItems().get(0);
    assertEquals("Avengers: The Initiative (2007) #14", firstComic.getName());
    assertEquals("http://gateway.marvel.com/v1/public/comics/21366", firstComic.getResourceUri());

    MarvelResources<StoryResource> stories = firstCharacter.getStories();
    assertEquals(17, stories.getAvailable());
    assertEquals(17, stories.getReturned());
    assertEquals("http://gateway.marvel.com/v1/public/characters/1011334/stories",
        stories.getCollectionUri());
    assertEquals(17, stories.getItems().size());
    StoryResource firstStory = stories.getItems().get(0);
    assertEquals("Cover #19947", firstStory.getName());
    assertEquals("http://gateway.marvel.com/v1/public/stories/19947", firstStory.getResourceUri());
    assertEquals("cover", firstStory.getType());
  }

  private List<Integer> getAnyEvents() {
    List<Integer> events = new ArrayList<>();
    events.add(ANY_EVENT_1);
    events.add(ANY_EVENT_2);
    return events;
  }

  private CharacterApiClient givenCharacterApiClient() {

    Retrofit retrofit = new Retrofit.Builder().baseUrl(getBaseEndpoint())
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    MarvelApiClient marvelApiClient =
        new MarvelApiClient.Builder(ANY_PUBLIC_KEY, ANY_PRIVATE_KEY).baseUrl(ANY_URL)
            .retrofit(retrofit)
            .build();
    return new CharacterApiClient(marvelApiClient);
  }

  public Date getAnyDate() {
    Calendar instance = Calendar.getInstance();
    instance.setTimeZone(TimeZone.getTimeZone(ANY_TIME_ZONE));
    instance.set(Calendar.YEAR, 2015);
    instance.set(Calendar.MONTH, Calendar.JANUARY);
    instance.set(Calendar.DAY_OF_MONTH, 9);
    instance.set(Calendar.HOUR, 10);
    instance.set(Calendar.MINUTE, 10);
    instance.set(Calendar.SECOND, 45);
    return instance.getTime();
  }
}